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
public class BrasilApiInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.info(
                "Brasil api request | {} {} | headers={}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders()
        );

        var start = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        var elapsed = System.currentTimeMillis();

        log.info(
                "Brasil api response | {} {} | status={} | time={}ms | headers={}",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode(),
                (elapsed - start),
                response.getHeaders()
        );

        return response;
    }
}
