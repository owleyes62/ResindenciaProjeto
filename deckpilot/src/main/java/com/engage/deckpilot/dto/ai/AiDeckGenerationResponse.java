package com.engage.deckpilot.dto.ai;

import java.util.List;

public record AiDeckGenerationResponse(
        String name,
        String archetype,
        String playStyle,
        String format,
        Integer mainDeckCount,
        Integer extraDeckCount,
        Integer sideDeckCount,
        String winCondition,
        String howToPilot,
        List<AiDeckCardResponse> mainDeck,
        List<AiDeckCardResponse> extraDeck,
        List<AiDeckCardResponse> sideDeck,
        String assistantMessage
) {
}
