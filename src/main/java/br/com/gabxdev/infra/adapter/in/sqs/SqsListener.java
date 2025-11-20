package br.com.gabxdev.infra.adapter.in.sqs;

import datadog.trace.api.Trace;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsListener {
    private volatile boolean running = true;

    private static final int MAX_MESSAGES_PER_POLL = 10;
    private static final int WAIT_TIME_SECONDS = 20;
    private static final int VISIBILITY_TIMEOUT_SECONDS = 80;

    private static final int DEFAULT_NUM_POLLERS = 2;

    private final SqsClient sqsClient;
    private final String queueUrl;
    private final ExecutorService messageExecutor;
    private final SqsConsumer consumer;

    private final List<Thread> pollerThreads = new CopyOnWriteArrayList<>();

    private volatile int numPollers = DEFAULT_NUM_POLLERS;

    @PostConstruct
    public void start() {
        log.info("Iniciando listener de SQS na fila {} com {} pollers (startup)", queueUrl, numPollers);
        startPollers(numPollers);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Parando listener SQS (shutdown do contexto)...");
        disable();               // só para o consumo
        messageExecutor.shutdown(); // encerra o executor de vez
    }

    // ===== Métodos para o Portal Manager =====

    /**
     * Desliga o listener (para de consumir mensagens).
     */
    public synchronized void disable() {
        if (!running) {
            return;
        }
        log.info("Desligando listener SQS via portal manager...");
        running = false;
        numPollers = DEFAULT_NUM_POLLERS;
        log.info("DEFAULT={}", DEFAULT_NUM_POLLERS);
        stopPollerThreads();
    }

    /**
     * Liga o listener com a quantidade atual de pollers configurada.
     */
    public synchronized void enable() {
        if (running) {
            return;
        }
        log.info("Ligando listener SQS via portal manager com {} pollers...", numPollers);
        running = true;
        startPollers(numPollers);
    }

    /**
     * Atualiza a quantidade de pollers em tempo de execução.
     */
    public synchronized void updatePollerCount(int newNumPollers) {
        if (newNumPollers <= 0) {
            throw new IllegalArgumentException("newNumPollers deve ser > 0");
        }
        log.info("Atualizando quantidade de pollers de {} para {} via portal manager",
                this.numPollers, newNumPollers);

        this.numPollers = newNumPollers;

        // se o listener estiver ligado, reinicia os pollers com o novo valor
        if (running) {
            stopPollerThreads();
            enable();
        }
    }

    // ===== Infra de threads dos pollers =====

    private void startPollers(int pollers) {
        for (int i = 0; i < pollers; i++) {
            Thread t = Thread.ofVirtual()
                    .name("sqs-poller-" + UUID.randomUUID())
                    .start(this::pollLoop);

            log.info("Starting poller - {}", t.getName());

            pollerThreads.add(t);
        }
    }

    private void stopPollerThreads() {
        // sinaliza para sair do while
        running = false;

        for (Thread t : pollerThreads) {
            t.interrupt();
        }

        pollerThreads.clear();
    }

    private void pollLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(MAX_MESSAGES_PER_POLL)
                        .waitTimeSeconds(WAIT_TIME_SECONDS)
                        .visibilityTimeout(VISIBILITY_TIMEOUT_SECONDS)
                        .build();

                ReceiveMessageResponse response = sqsClient.receiveMessage(request);

                List<Message> messages = response.messages();
                if (messages.isEmpty()) {
                    continue;
                }

                handleMessages(messages);

            } catch (SdkException e) {
                log.error("Erro AWS ao receber mensagens do SQS");
                sleepSilently(999);
            } catch (Exception e) {
                log.error("Erro inesperado no pollLoop do SQS");
                sleepSilently(1000);
            }
        }

        log.info("Poller SQS finalizado: {}", Thread.currentThread().getName());
    }

    private void handleMessages(List<Message> messages) {
        List<CompletableFuture<ProcessResult>> futures = new ArrayList<>(messages.size());

        for (Message msg : messages) {
            CompletableFuture<ProcessResult> future =
                    CompletableFuture.supplyAsync(() -> processMessage(msg), messageExecutor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        List<Message> mensagensProcessadasComSucesso = new ArrayList<>();

        for (int i = 0; i < futures.size(); i++) {
            ProcessResult result;

            result = futures.get(i).join();

            if (result.isSuccess()) {
                mensagensProcessadasComSucesso.add(messages.get(i));
            }

            if (!result.isSuccess()) {
                log.warn("Mensagem falhou e não será deletada. Ela será reentregue pelo SQS. body={}", messages.get(i).body(), result.getError());
            }
        }

        if (!mensagensProcessadasComSucesso.isEmpty()) {
            deleteBatch(mensagensProcessadasComSucesso);
        }
    }

    private ProcessResult processMessage(Message message) {
        var correlationId = extractCorrelationId(message);

        try {
            MDC.put("correlation_id", correlationId);
            org.jboss.logging.MDC.put("correlation_id", correlationId);

            doProcessMessage(message);

            return ProcessResult.success();
        } catch (Exception e) {
            log.error("Erro ao processar mensagens do SQS", e);

            return ProcessResult.failure(e);
        } finally {

            MDC.remove("correlation_id");
            org.jboss.logging.MDC.remove("correlation_id");
        }
    }

    @Trace(
            operationName = "sqs.consume",
            resourceName = "processar-evento-sqs",
            measured = true
    )
    private void doProcessMessage(Message message) {
        log.info("Iniciando processamento da mensagem SQS. correlation_id={}", MDC.get("correlation_id"));

        consumer.consume(message);
    }

    private void deleteBatch(List<Message> messages) {
        List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            entries.add(DeleteMessageBatchRequestEntry.builder()
                    .id("msg-" + i)
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }

        DeleteMessageBatchRequest deleteRequest = DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(entries)
                .build();

        try {
            DeleteMessageBatchResponse response = sqsClient.deleteMessageBatch(deleteRequest);

            if (!response.failed().isEmpty()) {
                response.failed().forEach(f ->
                        log.error("Falha ao deletar mensagem do SQS. id={}, code={}, message={}",
                                f.id(), f.code(), f.message())
                );
            }
        } catch (SdkException e) {
            log.error("Erro AWS ao deletar batch de mensagens", e);
        } catch (Exception e) {
            log.error("Error inesperado ao tentar deletar batch mensagens", e);
        }
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String extractCorrelationId(Message message) {
        if (message.messageAttributes() != null &&
            message.messageAttributes().containsKey("correlation_id")) {
            return message.messageAttributes()
                    .get("correlation_id")
                    .stringValue();
        }
        // fallback: se não vier, gera um novo ou retorna null
        return UUID.randomUUID().toString();
    }
}
