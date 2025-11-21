package br.com.gabxdev.infra.integration.client;

import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ApiBrasilClient {

    private final RestClient brasilApiRestClient;

    public EnderecoGetResponse buscarEndereco(String cep) {
        return brasilApiRestClient.get()
                .uri("/{cep}", cep)
                .retrieve()
                .onStatus(s -> !s.is2xxSuccessful(), (req, resp) -> {
                    throw HttpClientErrorException.create(
                            resp.getStatusCode(),
                            "Erro ao tentar buscar endereco - API BRASIL",
                            resp.getHeaders(),
                            null,
                            null
                    );
                })
                .body(EnderecoGetResponse.class);
    }
}
