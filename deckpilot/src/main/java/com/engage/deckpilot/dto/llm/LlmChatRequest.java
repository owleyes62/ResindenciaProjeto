package com.engage.deckpilot.dto.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LlmChatRequest(
        String model,
        List<LlmMessage> messages,
        Double temperature,
        @JsonProperty("max_tokens") Integer maxTokens
) {
}
