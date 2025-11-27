package br.com.gabxdev.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class Metrics {

    private final Counter eventosProcessadoRecebido;
    private final Counter eventosProcessadosSucesso;
    private final Counter eventosProcessadosErro;

    public Metrics(MeterRegistry registry) {
        this.eventosProcessadoRecebido = Counter.builder("sqs.eventos.recebidos")
                .description("Total de eventos recebidos do SQS")
                .register(registry);

        this.eventosProcessadosSucesso = Counter.builder("sqs.eventos.processados")
                .description("Total de eventos processados do SQS")
                .tag("resultado", "SUCESSO")
                .register(registry);

        this.eventosProcessadosErro = Counter.builder("sqs.eventos.processados")
                .description("Total de eventos processados do SQS")
                .tag("resultado", "ERRO")
                .register(registry);
    }

    public void incrementEventosRecebidos() {
        eventosProcessadoRecebido.increment();
    }

    public void incrementProcessadoSucesso() {
        eventosProcessadosSucesso.increment();
    }

    public void incrementProcessadoErro() {
        eventosProcessadosErro.increment();
    }
}
