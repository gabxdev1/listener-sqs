package br.com.gabxdev.domain.ports.out;

import br.com.gabxdev.domain.model.Contrato;

import java.util.List;
import java.util.Optional;

public interface ContratoRepositoryUseCase {

    void save(Contrato contrato);

    Optional<Contrato> findByCpf(String cpf);

    List<Contrato> findByStatus(String status);

    boolean existsByCpf(String cpf);
}
