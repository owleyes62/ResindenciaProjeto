package com.engage.deckpilot.dto.chat;

public record ChatSendMessageResponse(
        ChatMessageResponse userMessage,
        ChatMessageResponse assistantMessage,
        ChatGeneratedDeckResponse generatedDeck
) {
}
