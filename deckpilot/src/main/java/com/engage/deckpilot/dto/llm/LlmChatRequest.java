package com.engage.deckpilot.dto.llm;

import java.util.List;

public record LlmChatRequest(
        String model,
        List<LlmMessage> messages,
        Double temperature
) {
}
