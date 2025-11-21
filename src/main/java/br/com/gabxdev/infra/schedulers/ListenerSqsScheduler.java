package br.com.gabxdev.infra.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListenerSqsScheduler {

    @Scheduled(fixedRate = 30_000)
    public void executarTarefa() {
        MDC.put("task", "tarefa_10min");
        org.jboss.logging.MDC.put("task", "tarefa_10min");
        try {
            log.info("Iniciando tarefa.");

            // lógica...

            log.info("Tarefa concluída.");
        } finally {
            MDC.clear();
            org.jboss.logging.MDC.clear();
        }
    }
}
