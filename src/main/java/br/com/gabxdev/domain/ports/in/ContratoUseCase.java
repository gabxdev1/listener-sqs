package br.com.gabxdev.domain.ports.in;

import br.com.gabxdev.domain.model.Contrato;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;

import java.util.List;

public interface ContratoUseCase {
    void createContrato(ContratoEventConsumer request);

    Contrato findByCpf(String cpf);

    List<Contrato> findByStatus(String status);
}
