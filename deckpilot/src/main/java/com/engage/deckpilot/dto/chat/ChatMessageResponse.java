package com.engage.deckpilot.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt
) {
}
