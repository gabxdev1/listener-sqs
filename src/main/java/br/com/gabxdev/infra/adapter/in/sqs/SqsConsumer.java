package br.com.gabxdev.infra.adapter.in.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@Slf4j
public class SqsConsumer {

    public void consume(Message message) {
        var event = message.body();
        log.info("Consumido com sucesso. body={}", event);

        if (event.contains("20")) {
            throw new RuntimeException("Forçando error test");
        }
    }
}
