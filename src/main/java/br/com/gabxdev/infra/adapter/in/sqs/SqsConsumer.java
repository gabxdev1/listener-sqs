package br.com.gabxdev.infra.adapter.in.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@Slf4j
public class SqsConsumer {

    public void consume(Message message) {
        log.info("Consumido com sucesso. body={}", message.body());
    }
}
