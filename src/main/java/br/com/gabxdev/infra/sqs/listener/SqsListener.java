package br.com.gabxdev.infra.sqs.listener;

import br.com.gabxdev.infra.properties.SqsListenerProperties;
import br.com.gabxdev.domain.ports.SqsBatchProcessor;
import br.com.gabxdev.infra.utils.SleepUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsListener {
    private final SqsListenerProperties props;
    private final SqsClient sqsClient;
    private final SqsBatchProcessor batchProcessor;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private volatile int numPollers;

    private final List<Thread> pollerThreads = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void start() {
        this.numPollers = props.getDefaultNumPollers();

        log.info("Iniciando listener de SQS na fila {} com {} pollers (startup)", props.getQueueUrl(), numPollers);

        startPollers(numPollers);
    }

    @PreDestroy
    public void shutdown() {
        running.set(false);
        SleepUtils.sleep(500);
        stopPollerThreads();
    }

    public synchronized void recriarPollerLoop(int desiredNumPollers) {
        if (desiredNumPollers <= 0 || desiredNumPollers > 5) {
            log.warn("Valor desejado de pollers ({}) é inválido. Ignorando.", desiredNumPollers);
            return;
        }

        if (this.numPollers == desiredNumPollers) {
            return;
        }

        log.info("Detectada mudança na configuração de pollers: {} -> {}", this.numPollers, desiredNumPollers);

        updatePollerCount(desiredNumPollers);
    }

    private void updatePollerCount(int newNumPollers) {
        running.set(false);
        SleepUtils.sleep(15_000);
        this.numPollers = newNumPollers;
        stopPollerThreads();
        running.set(true);
        startPollers(newNumPollers);
    }

    private void startPollers(int pollers) {
        for (int i = 0; i < pollers; i++) {
            var name = "sqs-poller-" + UUID.randomUUID();

            var worker = new SqsPollerWorker(
                    sqsClient,
                    props,
                    batchProcessor,
                    running,
                    name
            );

            var t = Thread.ofVirtual()
                    .name(name)
                    .start(worker);

            pollerThreads.add(t);
        }
    }

    private void stopPollerThreads() {
        for (Thread t : pollerThreads) {
            t.interrupt();
        }
        pollerThreads.clear();
    }
}
