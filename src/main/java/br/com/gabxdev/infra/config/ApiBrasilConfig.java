package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.integration.interceptors.BrasilApiInterceptor;
import br.com.gabxdev.infra.properties.BrasilApiProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@RequiredArgsConstructor
public class ApiBrasilConfig {

    private final BrasilApiProperties props;

//    @Bean
//    public ClientHttpRequestFactory externalApiRequestFactory() {
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(props.getConnectTimeoutMs())
//                .setConnectionRequestTimeout(props.getReadTimeoutMs())
//                .build();
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setDefaultRequestConfig(requestConfig)
//                .setMaxConnTotal(props.getMaxConnTotal())
//                .setMaxConnPerRoute(props.getMaxConnPerRoute())
//                .build();
//
//        return new HttpComponentsClientHttpRequestFactory();
//    }


    @Bean
    public RestClient brasilApiRestClient(
//            ClientHttpRequestFactory externalApiRequestFactory,
            BrasilApiInterceptor brasilApiInterceptor
    ) {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
//                .requestFactory(externalApiRequestFactory)
                .requestInterceptor(brasilApiInterceptor)
                .build();
    }
}
