package br.com.gabxdev.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EnderecoGetResponse(
        String city,

        String street
) {
}
