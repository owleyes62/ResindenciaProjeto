package com.engage.deckpilot.dto.chat;

import java.time.LocalDateTime;

public record ChatSessionResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
