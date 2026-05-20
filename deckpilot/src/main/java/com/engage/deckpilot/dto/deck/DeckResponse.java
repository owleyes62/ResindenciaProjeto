package com.engage.deckpilot.dto.deck;

import java.time.LocalDateTime;
import java.util.List;

public record DeckResponse(
        Long id,
        String name,
        String archetype,
        String playStyle,
        String format,
        String winCondition,
        String howToPilot,
        String source,
        List<DeckCardResponse> mainDeck,
        List<DeckCardResponse> extraDeck,
        List<DeckCardResponse> sideDeck,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}