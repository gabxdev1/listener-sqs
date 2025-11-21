package br.com.gabxdev.infra.adapter.out.rest;

import br.com.gabxdev.domain.ports.out.BrasilApiUseCase;
import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import br.com.gabxdev.infra.integration.client.ApiBrasilClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrasilApiService implements BrasilApiUseCase {

    private final ApiBrasilClient client;

    @Override
    public EnderecoGetResponse findEndereoByCep(String cep) {
        return client.buscarEndereco(cep);
    }
}
