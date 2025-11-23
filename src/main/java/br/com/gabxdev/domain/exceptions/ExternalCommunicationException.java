package br.com.gabxdev.domain.exceptions;

public class ExternalCommunicationException extends RuntimeException {
    public ExternalCommunicationException(String message) {
        super(message);
    }
}