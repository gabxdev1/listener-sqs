package br.com.gabxdev.infra.adapter.in.sqs;

import br.com.gabxdev.domain.ports.in.ContratoUseCase;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.utils.JsonUtils;
import br.com.gabxdev.infra.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsController {

    private final JsonUtils jsonUtils;

    private final ContratoUseCase service;

    private final LogUtils logUtils;

    public void consume(Message message) {
        log.info("{}", logUtils.logger("INICIO_PROCESSAMENTO_SQS", "Iniciamendo processamento do evento"));

        var contratoRequest = jsonUtils.toObject(message.body(), ContratoEventConsumer.class);

        service.createContrato(contratoRequest);

        log.info("{}", logUtils.logger("FINAL_PROCESSAMENTO_SQS", "Evento processado com sucesso"));
    }
}
