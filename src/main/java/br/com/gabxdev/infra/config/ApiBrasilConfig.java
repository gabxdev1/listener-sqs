package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.integration.interceptors.BrasilApiInterceptor;
import br.com.gabxdev.infra.integration.interceptors.TestBrasilApiInterceptor;
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
    public RestClient brasilApiRestClient(
            BrasilApiInterceptor brasilApiInterceptor,
            RestClient.Builder builder
    ) {
        RestClient.Builder clientClone = builder.clone();

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(props.getResponseTimeoutMs()));

        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        return clientClone
                .requestFactory(factory)
                .baseUrl(props.getBaseUrl())
                .requestInterceptor(brasilApiInterceptor)
                .build();

    }

    @Bean
    public RestClient sPApiRestClient(
            TestBrasilApiInterceptor testBrasilApiInterceptor,
            RestClient.Builder builder
    ) {
        RestClient.Builder clientSpClone = builder.clone();

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(props.getResponseTimeoutMs()));

        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        return clientSpClone
                .requestFactory(factory)
                .baseUrl(props.getBaseUrl())
                .requestInterceptor(testBrasilApiInterceptor)
                .build();

    }
}
