package br.com.gabxdev.infra.sqs.listener;

import br.com.gabxdev.domain.ports.SqsBatchProcessor;
import br.com.gabxdev.infra.properties.SqsListenerProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static br.com.gabxdev.infra.utils.SleepUtils.sleep;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsListener {
    private final SqsListenerProperties props;
    private final SqsClient sqsClient;
    private final SqsBatchProcessor batchProcessor;

    private final List<Thread> pollerThreads = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void start() {
        log.info("Iniciando listener de SQS na fila {} com {} pollers (startup)", props.getQueueUrl(), props.getNumPollers());

        startPollers(props.getNumPollers());
    }

    @PreDestroy
    public void shutdown() {
        props.setRunning(false);
        sleep(500);
        stopPollerThreads();
    }

    public synchronized void listenerConfigUpdate(int maxMessagesPerPoll,
                                                  int desiredNumPollers,
                                                  int backOff) {
        if (props.getMaxMessagesPerPoll() != maxMessagesPerPoll) {
            if (maxMessagesPerPoll >= 1 && maxMessagesPerPoll <= 10) {
                log.info("Decteada mudança na configuração de maxMessagesPerPoll: {} -> {}", props.getMaxMessagesPerPoll(), maxMessagesPerPoll);

                props.setMaxMessagesPerPoll(maxMessagesPerPoll);
            }
        }

        if (props.getBackOff() != backOff) {
            if (backOff >= 0) {
                log.info("Decteada mudança na configuração de backOff: {} -> {}", props.getBackOff(), backOff);

                props.setBackOff(backOff);
            }
        }

        if (props.getNumPollers() != desiredNumPollers) {
            if (desiredNumPollers >= 1 && desiredNumPollers <= 5) {
                log.info("Detectada mudança na configuração de pollers: {} -> {}", props.getNumPollers(), desiredNumPollers);

                updatePollerCount(desiredNumPollers);
            }
        }
    }

    private void updatePollerCount(int newNumPollers) {
        props.setRunning(false);
        props.setNumPollers(newNumPollers);
        sleep(15_000);
        stopPollerThreads();
        props.setRunning(true);
        startPollers(newNumPollers);
    }

    private void startPollers(int pollers) {
        for (int i = 0; i < pollers; i++) {
            var name = "sqs-poller-" + UUID.randomUUID();

            var worker = new SqsPollerWorker(
                    sqsClient,
                    props,
                    batchProcessor,
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
