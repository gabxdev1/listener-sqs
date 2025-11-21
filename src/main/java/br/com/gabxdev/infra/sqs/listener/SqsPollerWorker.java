package br.com.gabxdev.infra.sqs.listener;

import br.com.gabxdev.infra.properties.SqsListenerProperties;
import br.com.gabxdev.domain.ports.SqsBatchProcessor;
import br.com.gabxdev.infra.utils.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SqsPollerWorker implements Runnable {

    private final SqsClient sqsClient;
    private final SqsListenerProperties props;
    private final SqsBatchProcessor batchProcessor;
    private final AtomicBoolean running;
    private final String name;

    public SqsPollerWorker(SqsClient sqsClient, SqsListenerProperties props, SqsBatchProcessor batchProcessor, AtomicBoolean running, String name) {
        this.sqsClient = sqsClient;
        this.props = props;
        this.batchProcessor = batchProcessor;
        this.running = running;
        this.name = name;
    }

    @Override
    public void run() {
        log.info("Poller SQS iniciado: {}", name);

        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                var request = ReceiveMessageRequest.builder()
                        .queueUrl(props.getQueueUrl())
                        .maxNumberOfMessages(props.getMaxMessagesPerPoll())
                        .waitTimeSeconds(props.getWaitTimeSeconds())
                        .visibilityTimeout(props.getVisibilityTimeoutSeconds())
                        .build();

                var response = sqsClient.receiveMessage(request);

                var messages = response.messages();
                if (messages.isEmpty()) {
                    continue;
                }

                batchProcessor.processBatch(messages);

            } catch (SdkException e) {
                log.error("Erro AWS ao receber mensagens do SQS", e);
                SleepUtils.sleep(1000);
            } catch (Exception e) {
                log.error("Erro inesperado no pollLoop do SQS", e);
                SleepUtils.sleep(1000);
            }
        }
        System.out.println(running);
        log.info("Poller SQS finalizado: {}", name);
    }
}
