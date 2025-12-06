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

    @Bean
    public RestClient brasilApiRestClient(
            BrasilApiInterceptor brasilApiInterceptor,
            BrasilApiProperties props,
            RestClient.Builder restClientBuilder
    ) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(props.getResponseTimeoutMs()));

        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        return restClientBuilder
                .clone()
                .requestFactory(factory)
                .baseUrl(props.getBaseUrl())
                .requestInterceptor(brasilApiInterceptor)
                .build();
    }

}