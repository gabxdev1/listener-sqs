package br.com.gabxdev.infra.integration.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TestBrasilApiInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        log.info("TesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTestttttttttttttttttttttttttttttttttttttttttt");

        ClientHttpResponse response = execution.execute(request, body);

        log.info("TesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTesttttttttttttttttttttttttttttttttttttttttttTestttttttttttttttttttttttttttttttttttttttttt");

        return response;
    }
}