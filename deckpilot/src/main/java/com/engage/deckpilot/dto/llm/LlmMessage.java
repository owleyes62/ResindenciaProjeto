package com.engage.deckpilot.dto.llm;

public record LlmMessage(
        String role,
        String content
) {
}
