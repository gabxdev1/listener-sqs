package br.com.gabxdev.domain.exceptions;

public class ExternalClientException extends RuntimeException {
    public ExternalClientException(String message) {
        super(message);
    }
}
