package br.com.gabxdev.infra.integration.client;

import br.com.gabxdev.domain.exceptions.ExternalCommunicationException;
import br.com.gabxdev.domain.exceptions.ExternalTimeoutException;
import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiBrasilClient {

    private final RestClient brasilApiRestClient;

    public EnderecoGetResponse buscarEndereco(String cep) {

        try {
            return brasilApiRestClient.get()
                    .uri("/{cep}", cep)
                    .retrieve()
                    .body(EnderecoGetResponse.class);

        } catch (ResourceAccessException ex) {
            if (isTimeout(ex)) {
                log.error("Timeout chamando API Brasil. {}", ex.getMessage() == null ? "TIMEOUT" :
                        ex.getMessage(), ex);

                throw new ExternalTimeoutException("Timeout chamando API Brasil");
            }

            log.error("Erro de comunicação com API Brasil. {}", ex.getMessage(), ex);

            throw new ExternalCommunicationException("Erro de comunicação com API Brasil");
        } catch (Exception ex) {
            log.error("Erro inesperado ao chamar API Brasil. {}", ex.getMessage(), ex);

            throw new ExternalCommunicationException("Erro inesperado ao chamar API Brasil");
        }
    }

    private boolean isTimeout(ResourceAccessException ex) {
        var cause = ex.getCause();

        return cause instanceof SocketTimeoutException ||
               cause instanceof HttpTimeoutException;
    }
}
