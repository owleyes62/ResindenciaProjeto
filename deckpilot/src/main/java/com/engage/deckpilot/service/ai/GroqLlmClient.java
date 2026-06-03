package com.engage.deckpilot.service.ai;

import com.engage.deckpilot.config.LlmProperties;
import com.engage.deckpilot.dto.llm.LlmChatRequest;
import com.engage.deckpilot.dto.llm.LlmChatResponse;
import com.engage.deckpilot.dto.llm.LlmMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroqLlmClient {

    private final LlmProperties llmProperties;

    public String chatForDoctor(String systemPrompt, String userPrompt) {
        LlmProperties.LlmProviderConfig config = llmProperties.getDoctor();

        return callChatCompletion(
                config,
                List.of(
                        new LlmMessage("system", systemPrompt),
                        new LlmMessage("user", userPrompt)
                )
        );
    }

    public String chatForDeckGeneration(String systemPrompt, String userPrompt) {
        LlmProperties.LlmProviderConfig config = llmProperties.getChat();

        return callChatCompletion(
                config,
                List.of(
                        new LlmMessage("system", systemPrompt),
                        new LlmMessage("user", userPrompt)
                )
        );
    }

    private String callChatCompletion(
            LlmProperties.LlmProviderConfig config,
            List<LlmMessage> messages
    ) {
        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new IllegalStateException("Groq API key is not configured.");
        }

        LlmChatRequest request = new LlmChatRequest(
                config.getModel(),
                messages,
                0.3,
                4096
        );

        RestClient restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        LlmChatResponse response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LlmChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("Empty response from LLM provider.");
        }

        LlmChatResponse.Message message = response.choices().get(0).message();

        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new IllegalStateException("LLM response has no content.");
        }

        return message.content();
    }
}
