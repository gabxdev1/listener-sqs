package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.integration.interceptors.BrasilApiInterceptor;
import br.com.gabxdev.infra.properties.BrasilApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiBrasilConfig {

    private final BrasilApiProperties props;

    @Bean
    public RestClient.Builder restClientBuilder() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(props.getResponseTimeoutMs()));

        ClientHttpRequestFactory factory =
                ClientHttpRequestFactoryBuilder.detect().build(settings);


        return RestClient.builder()
                .requestFactory(factory);
//                .defaultStatusHandler(
//                        HttpStatusCode::is4xxClientError,
//                        (request, response) -> {
//                            response.getBody();
//                            String body = new String(response.getBody().readAllBytes());
//
//                            log.error("API - BRASIL -  Erro 4xx: {} body={}", response.getStatusCode(), body);
//
//                            throw new ExternalClientException(
//                                    "API - BRASIL -  Erro 4xx: " + response.getStatusCode() + " body=" + body
//                            );
//                        }
//                )
//                .defaultStatusHandler(
//                        HttpStatusCode::is5xxServerError,
//                        (request, response) -> {
//                            response.getBody();
//                            String body = new String(response.getBody().readAllBytes());
//
//                            log.error("API - BRASIL -  Erro 5xx: {} body={}", response.getStatusCode(), body);
//
//                            throw new ExternalServerException(
//                                    "API - BRASIL -  Erro 5xx: " + response.getStatusCode() + " body=" + body
//                            );
//                        }
//                );
    }


    @Bean
    public RestClient brasilApiRestClient(
            BrasilApiInterceptor brasilApiInterceptor,
            RestClient.Builder restClientBuilder
    ) {
        return restClientBuilder
                .baseUrl(props.getBaseUrl())
                .requestInterceptor(brasilApiInterceptor)
                .build();
    }
}
