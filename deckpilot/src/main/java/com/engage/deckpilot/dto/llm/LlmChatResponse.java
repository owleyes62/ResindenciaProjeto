package com.engage.deckpilot.dto.llm;

import java.util.List;

public record LlmChatResponse(
        List<Choice> choices
) {
    public record Choice(
            Message message
    ) {
    }

    public record Message(
            String role,
            String content
    ) {
    }
}
