package com.engage.deckpilot.dto.deck;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeckCardRequest(
        @NotBlank
        String cardName,

        @NotNull
        @Min(1)
        Integer copies
) {
}