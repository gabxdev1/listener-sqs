package br.com.gabxdev.infra.sqs;

import br.com.gabxdev.domain.ports.out.PollerConfigPort;
import br.com.gabxdev.infra.sqs.listener.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsPollerConfigScheduler {
    private final PollerConfigPort pollerConfigPort;

    private final SqsListener sqsListener;

    @Scheduled(fixedRate = 30_000)
    public void checkPollerConfig() {
        try {
            int desired = pollerConfigPort.getDesiredNumPollers();

            sqsListener.recriarPollerLoop(desired);
        } catch (Exception e) {
            log.error("Erro ao verificar/atualizar configuração de pollers SQS", e);
        }
    }
}
