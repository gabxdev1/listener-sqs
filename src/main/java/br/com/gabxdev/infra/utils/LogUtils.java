package br.com.gabxdev.infra.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogUtils {

    private final JsonUtils jsonUtils;

    public String logger(String logCode, String logMessage) {
        var log = LogPattern.logger(logCode, logMessage);

        return jsonUtils.toJson(log);
    }
}
