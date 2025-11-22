package br.com.gabxdev.infra.sqs.listener;

import br.com.gabxdev.infra.properties.SqsListenerProperties;
import br.com.gabxdev.domain.ports.SqsBatchProcessor;
import br.com.gabxdev.infra.utils.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.atomic.AtomicBoolean;

import static br.com.gabxdev.infra.utils.SleepUtils.sleep;

@Slf4j
public class SqsPollerWorker implements Runnable {

    private final SqsClient sqsClient;
    private final SqsListenerProperties props;
    private final SqsBatchProcessor batchProcessor;
    private final String name;

    public SqsPollerWorker(SqsClient sqsClient, SqsListenerProperties props, SqsBatchProcessor batchProcessor, String name) {
        this.sqsClient = sqsClient;
        this.props = props;
        this.batchProcessor = batchProcessor;
        this.name = name;
    }

    @Override
    public void run() {
        log.info("Poller SQS iniciado: {}", name);

        while (props.isRunning() && !Thread.currentThread().isInterrupted()) {
            try {
                log.info("MAX MESSAGES: {}", props.getMaxMessagesPerPoll());
                log.info("BACK OFF: {}", props.getBackOff());

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
                sleep(1000);
            } catch (Exception e) {
                log.error("Erro inesperado no pollLoop do SQS", e);
                sleep(1000);
            }
        }
        log.info("Poller SQS finalizado: {}", name);
    }
}
