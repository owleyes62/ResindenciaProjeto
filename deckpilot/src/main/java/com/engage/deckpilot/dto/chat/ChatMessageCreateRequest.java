package com.engage.deckpilot.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageCreateRequest(
        @NotBlank
        String content
) {
}
