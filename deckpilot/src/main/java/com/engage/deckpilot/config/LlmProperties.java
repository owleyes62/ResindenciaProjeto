package com.engage.deckpilot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.llm")
public class LlmProperties {

    private LlmProviderConfig chat = new LlmProviderConfig();
    private LlmProviderConfig doctor = new LlmProviderConfig();

    @Getter
    @Setter
    public static class LlmProviderConfig {
        private String provider;
        private String apiKey;
        private String model;
        private String baseUrl;
    }
}
