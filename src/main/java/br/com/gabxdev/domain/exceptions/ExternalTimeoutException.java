package br.com.gabxdev.domain.exceptions;

import org.springframework.web.client.ResourceAccessException;

public class ExternalTimeoutException extends RuntimeException {
    public ExternalTimeoutException(String message) {
        super(message);
    }
}