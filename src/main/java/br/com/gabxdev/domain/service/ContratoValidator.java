package br.com.gabxdev.domain.service;

import jakarta.validation.ValidationException;

public class ContratoValidator {

    public void afirmoQueNaoExisteContratoByCpf(boolean exists) {
        if (exists) {
            throw new ValidationException("Já exite um client com este CPF!");
        }
    }
}
