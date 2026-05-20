package com.engage.deckpilot.dto.chat;

import com.engage.deckpilot.dto.deck.DeckResponse;

import java.time.LocalDateTime;

public record ChatGeneratedDeckResponse(
        Long id,
        Long sessionId,
        Integer generationIndex,
        DeckResponse deck,
        Long userMessageId,
        Long assistantMessageId,
        LocalDateTime createdAt
) {
}
