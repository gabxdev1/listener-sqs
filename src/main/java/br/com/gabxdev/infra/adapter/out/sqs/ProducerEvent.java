package br.com.gabxdev.infra.adapter.out.sqs;

import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.properties.SqsListenerProperties;
import br.com.gabxdev.infra.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

import static br.com.gabxdev.infra.utils.SleepUtils.sleep;

@Component
@RequiredArgsConstructor
public class ProducerEvent {

    private final SqsClient sqsClient;

    private final SqsListenerProperties props;

    private final JsonUtils jsonUtils;

    @PostConstruct
    public void start() {
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
//        Thread.startVirtualThread(this::send);
    }


    public void send() {
        while (true) {
            var event = new ContratoEventConsumer(
                    UUID.randomUUID().toString(),
                    "37048330",
                    UUID.randomUUID().toString()
            );

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(props.getQueueUrl())
                    .messageBody(jsonUtils.toJson(event))
                    .build();

            sqsClient.sendMessage(request);

            sleep(100);
        }

    }
}
