package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.integration.interceptors.BrasilApiInterceptor;
import br.com.gabxdev.infra.properties.BrasilApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.*;

@Configuration
@RequiredArgsConstructor
public class ApiBrasilConfig {

    @Bean(destroyMethod = "close")
    ExecutorService httpClientWriteVt() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(destroyMethod = "close")
    ExecutorService httpClientCompletionPool() {
        return Executors.newFixedThreadPool(8);
    }

    @Bean
    public HttpClient jdkHttpClient(ExecutorService httpClientCompletionPool) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(500))
                .executor(httpClientCompletionPool)
                .followRedirects(HttpClient.Redirect.NEVER)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory requestFactory(HttpClient jdkHttpClient, ExecutorService httpClientWriteVt) {
        JdkClientHttpRequestFactory jdkClientHttpRequestFactory = new JdkClientHttpRequestFactory(jdkHttpClient, httpClientWriteVt);

        jdkClientHttpRequestFactory.setReadTimeout(500);

        return jdkClientHttpRequestFactory;
    }

    @Bean
    public RestClient brasilApiRestClient(
            BrasilApiInterceptor brasilApiInterceptor,
            BrasilApiProperties props,
            ClientHttpRequestFactory requestFactory
    ) {
        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(props.getBaseUrl())
                .requestInterceptor(brasilApiInterceptor)
                .build();
    }
}
