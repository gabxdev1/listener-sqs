package br.com.gabxdev.infra.sqs;

import br.com.gabxdev.domain.model.ProcessResult;
import br.com.gabxdev.domain.ports.SqsBatchProcessor;
import br.com.gabxdev.domain.ports.SqsMessageProcessor;
import br.com.gabxdev.infra.properties.SqsListenerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static br.com.gabxdev.infra.utils.SleepUtils.sleep;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParallelSqsBatchProcessor implements SqsBatchProcessor {

    private final SqsMessageProcessor messageProcessor;
    private final SqsClient sqsClient;
    private final SqsListenerProperties props;
    private final ExecutorService messageExecutor;

    @Override
    public void processBatch(List<Message> messages) {
        List<CompletableFuture<ProcessResult>> futures = new ArrayList<>(messages.size());

        for (Message msg : messages) {
            CompletableFuture<ProcessResult> future = CompletableFuture.supplyAsync(() -> messageProcessor.process(msg), messageExecutor);

            sleep(props.getBackOff());

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

//        List<Message> mensagensProcessadasComSucesso = new ArrayList<>();
//
//        for (int i = 0; i < futures.size(); i++) {
//            ProcessResult result = futures.get(i).join();
//
//            if (result.isSuccess()) {
//                mensagensProcessadasComSucesso.add(messages.get(i));
//            }
//
//            if (!result.isSuccess()) {
//                mensagensProcessadasComSucesso.add(messages.get(i));
//                log.warn("Mensagem falhou e não será deletada. Ela será reentregue pelo SQS. body={}", messages.get(i).body(), result.getError());
//            }
//        }

//        if (!mensagensProcessadasComSucesso.isEmpty()) {
//            deleteBatch(mensagensProcessadasComSucesso);
//        }

        deleteBatch(messages);
    }

    private void deleteBatch(List<Message> messages) {
        List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();

        for (Message msg : messages) {
            entries.add(DeleteMessageBatchRequestEntry.builder()
                    .id(UUID.randomUUID().toString())
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }

        DeleteMessageBatchRequest deleteRequest = DeleteMessageBatchRequest.builder()
                .queueUrl(props.getQueueUrl())
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
            log.error("Erro inesperado ao tentar deletar batch de mensagens", e);
        }
    }
}
