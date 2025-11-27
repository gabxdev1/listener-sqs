package br.com.gabxdev.infra.sqs;

import br.com.gabxdev.domain.model.ProcessResult;
import br.com.gabxdev.domain.ports.SqsMessageProcessor;
import br.com.gabxdev.infra.adapter.in.sqs.SqsController;
import br.com.gabxdev.infra.metrics.Metrics;
import datadog.trace.api.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TracingSqsMessageProcessor implements SqsMessageProcessor {

    private final SqsController consumer;

    private final Metrics metrics;

    @Override
    public ProcessResult process(Message message) {
        var correlationId = extractCorrelationId(message);

        try {
            MDC.put("correlation_id", correlationId);
            org.jboss.logging.MDC.put("correlation_id", correlationId);

            doProcessMessage(message);

            return ProcessResult.success();
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do SQS", e);
            return ProcessResult.failure(e);
        } finally {
            MDC.remove("correlation_id");
            org.jboss.logging.MDC.remove("correlation_id");
        }
    }

    @Trace(
            operationName = "sqs.consume",
            resourceName = "processar-evento-sqs",
            measured = true
    )
    private void doProcessMessage(Message message) {
        metrics.incrementEventosRecebidos();

        try {
            consumer.consume(message);

            metrics.incrementProcessadoSucesso();
        } catch (Exception e) {
            metrics.incrementProcessadoErro();

            throw e;
        }
    }

    private String extractCorrelationId(Message message) {
        if (message.messageAttributes() != null &&
            message.messageAttributes().containsKey("correlation_id")) {
            return message.messageAttributes()
                    .get("correlation_id")
                    .stringValue();
        }
        return UUID.randomUUID().toString();
    }
}
