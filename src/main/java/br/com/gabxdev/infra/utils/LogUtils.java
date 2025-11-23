package br.com.gabxdev.infra.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogUtils {

    public String logger(String logCode, String logMessage) {
        return new StringBuilder("{")
                .append("\"log_code\":\"").append(logCode).append("\",")
                .append("\"message\":\"").append(logMessage).append("\"")
                .append("}")
                .toString();
    }
}
