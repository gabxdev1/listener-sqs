package br.com.gabxdev.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("sqs-contrato")
public class SqsListenerProperties {
    private String queueUrl;
    private int maxMessagesPerPoll = 1;
    private int waitTimeSeconds = 20;
    private int visibilityTimeoutSeconds = 80;
    private int numPollers = 1;
    private volatile boolean running = true;
    private int backOff = 10000;
}
