package br.com.gabxdev.domain.exceptions;

public class ExternalServerException extends RuntimeException {
    public ExternalServerException(String message) {
        super(message);
    }
}
