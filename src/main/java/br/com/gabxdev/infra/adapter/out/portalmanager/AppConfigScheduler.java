package br.com.gabxdev.infra.adapter.out.portalmanager;

import br.com.gabxdev.domain.ports.out.PollerConfigPort;
import br.com.gabxdev.infra.sqs.listener.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
            var desired = pollerConfigPort.getDesiredNumPollers();

            updateRootLogLevel(desired.logLevel());

            sqsListener.listenerConfigUpdate(desired.maxMessagesPerPoll(), desired.numPollers(), desired.backOff());
        } catch (Exception e) {
            log.error("Erro ao verificar/atualizar configuração da APP", e);
        }
    }
}
