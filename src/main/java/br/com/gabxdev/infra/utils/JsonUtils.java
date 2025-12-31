package br.com.gabxdev.infra.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public <T> T toObject(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    public String toJson(Object log) {
        return objectMapper.writeValueAsString(log);
    }
}
