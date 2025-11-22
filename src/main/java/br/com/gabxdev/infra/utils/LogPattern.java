package br.com.gabxdev.infra.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogPattern(@JsonProperty("log_code") String logCode,
                         @JsonProperty("message") String logMessage) {

    public static LogPattern logger(String logCode, String logMessage) {
        return new LogPattern(logCode, logMessage);
    }
}
