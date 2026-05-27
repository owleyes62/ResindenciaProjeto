package com.engage.deckpilot.service.ai;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptLoader {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String load(String resourcePath) {
        return cache.computeIfAbsent(resourcePath, this::readFromClasspath);
    }

    public String render(String resourcePath, Map<String, String> variables) {
        String template = load(resourcePath);

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{" + entry.getKey() + "}", value);
        }

        return result;
    }

    private String readFromClasspath(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load prompt: " + resourcePath, e);
        }
    }
}
