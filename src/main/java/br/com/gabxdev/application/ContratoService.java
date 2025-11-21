package br.com.gabxdev.application;

import br.com.gabxdev.domain.model.Contrato;
import br.com.gabxdev.domain.ports.in.ContratoUseCase;
import br.com.gabxdev.domain.ports.out.BrasilApiUseCase;
import br.com.gabxdev.domain.ports.out.ContratoRepositoryUseCase;
import br.com.gabxdev.domain.service.ContratoValidator;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.mapper.ContratoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

public class ContratoService implements ContratoUseCase {

    private final ContratoRepositoryUseCase repository;

    private final BrasilApiUseCase brasilApiService;

    private final ContratoValidator validator;

    private final ContratoMapper mapper;

    public ContratoService(ContratoRepositoryUseCase repository, BrasilApiUseCase brasilApiService, ContratoValidator validator, ContratoMapper mapper) {
        this.repository = repository;
        this.brasilApiService = brasilApiService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public void createContrato(ContratoEventConsumer request) {
        validator.afirmoQueNaoExisteContratoByCpf(existsByCpf(request.cpf().toLowerCase()));

        var endereco = brasilApiService.findEndereoByCep(request.cep());

        var contrato = mapper.toContrato(request, endereco, UUID.randomUUID().toString(), "A");

        repository.save(contrato);
    }

    @Override
    public Contrato findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato inexistente"));
    }

    @Override
    public List<Contrato> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    private boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }
}
