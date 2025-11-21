package br.com.gabxdev.domain.ports.out;

import br.com.gabxdev.infra.dto.EnderecoGetResponse;

public interface BrasilApiUseCase {

    EnderecoGetResponse findEndereoByCep(String cep);
}
