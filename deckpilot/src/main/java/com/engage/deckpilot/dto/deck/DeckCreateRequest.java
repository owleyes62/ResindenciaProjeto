package com.engage.deckpilot.dto.deck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DeckCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String archetype,

        @NotBlank
        String playStyle,

        String format,

        String winCondition,

        String howToPilot,

        List<@Valid DeckCardRequest> mainDeck,

        List<@Valid DeckCardRequest> extraDeck,

        List<@Valid DeckCardRequest> sideDeck
) {
}