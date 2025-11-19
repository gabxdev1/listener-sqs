package br.com.gabxdev.infra.config;

import org.springframework.beans.factory.annotation.Value;
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
public class SqsConfig {

    @Value("${sqs.queue-url}")
    private String queueUrl;

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

    @Bean
    public String sqsQueueUrl() {
        return queueUrl;
    }

    @Bean
    public ExecutorService messageExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
