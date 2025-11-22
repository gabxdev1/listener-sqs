package br.com.gabxdev.infra.dto;

public record AppConfigDto(
        int maxMessagesPerPoll,
        int numPollers,
        int backOff,
        String logLevel
) {
}
