package br.com.gabxdev.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("brasil-api")
public class BrasilApiProperties {
    private String baseUrl;
    private int connectTimeoutMs;
    private int readTimeoutMs;
    private int maxConnTotal;
    private int maxConnPerRoute;
}
