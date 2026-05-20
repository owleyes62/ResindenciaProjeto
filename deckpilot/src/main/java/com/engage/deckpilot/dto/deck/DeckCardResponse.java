package com.engage.deckpilot.dto.deck;

public record DeckCardResponse(
        Long cardId,
        String cardName,
        Integer copies,
        String section
) {
}