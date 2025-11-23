package br.com.gabxdev.infra.adapter.out.portalmanager;

import br.com.gabxdev.domain.ports.out.PollerConfigPort;
import br.com.gabxdev.infra.sqs.listener.SqsListener;
import datadog.trace.api.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static br.com.gabxdev.infra.utils.LogLevelUtil.updateRootLogLevel;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppConfigScheduler {

    private final PollerConfigPort pollerConfigPort;

    private final SqsListener sqsListener;

    @Scheduled(fixedRate = 30_000)
    public void checkPollerConfig() {
        try {
            MDC.put("correlation_id", UUID.randomUUID().toString());
            org.jboss.logging.MDC.put("correlation_id", UUID.randomUUID().toString());
            doCheckPollerConfig();
        } catch (Exception e) {
            log.error("Erro ao verificar/atualizar configuração da APP", e);
        } finally {
            MDC.remove("correlation_id");
            org.jboss.logging.MDC.remove("correlation_id");
        }
    }

//    @Trace(
//            operationName = "app.config.check",
//            resourceName = "atualizar-configuracao-app",
//            measured = true
//    )
    private void doCheckPollerConfig() {
        var desired = pollerConfigPort.getDesiredNumPollers();
        updateRootLogLevel(desired.logLevel());
        sqsListener.listenerConfigUpdate(desired.maxMessagesPerPoll(), desired.numPollers(), desired.backOff());
    }
}
