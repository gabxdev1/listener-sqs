package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.properties.SqsListenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class SqsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClientBuilder(
                        ApacheHttpClient.builder()
                                .maxConnections(256)
                                .connectionTimeout(Duration.ofSeconds(5))
                                .socketTimeout(Duration.ofSeconds(35))
                )
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService messageExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
