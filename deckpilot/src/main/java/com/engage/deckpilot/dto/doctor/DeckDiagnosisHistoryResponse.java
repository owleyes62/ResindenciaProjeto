package com.engage.deckpilot.dto.doctor;

import java.time.LocalDateTime;

public record DeckDiagnosisHistoryResponse(
        Long id,
        Long deckId,
        String summary,
        String source,
        LocalDateTime createdAt
) {
}
