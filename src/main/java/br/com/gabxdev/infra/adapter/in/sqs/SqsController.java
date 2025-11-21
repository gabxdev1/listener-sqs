package br.com.gabxdev.infra.adapter.in.sqs;

import br.com.gabxdev.domain.ports.in.ContratoUseCase;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsController {

    private final JsonUtils jsonUtils;

    private final ContratoUseCase service;

    public void consume(Message message) {
        log.info("Iniciando processamento da mensagem SQS. correlation_id={}", MDC.get("correlation_id"));

        var contratoRequest = jsonUtils.toObject(message.body(), ContratoEventConsumer.class);

        service.createContrato(contratoRequest);

        log.info("Processamento da mensagem SQS finalizado com sucesso. correlation_id={}", MDC.get("correlation_id"));

    }
}
