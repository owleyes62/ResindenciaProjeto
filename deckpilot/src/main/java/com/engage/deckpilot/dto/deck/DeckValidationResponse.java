package com.engage.deckpilot.dto.deck;

import java.util.List;

public record DeckValidationResponse(
        boolean valid,
        int mainDeckCount,
        int extraDeckCount,
        int sideDeckCount,
        List<String> errors,
        List<String> warnings
) {
}