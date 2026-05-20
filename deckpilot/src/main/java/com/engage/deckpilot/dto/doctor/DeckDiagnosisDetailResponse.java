package com.engage.deckpilot.dto.doctor;

import java.time.LocalDateTime;

public record DeckDiagnosisDetailResponse(
        Long id,
        Long deckId,
        String deckName,
        String summary,
        String strengths,
        String risks,
        String suggestions,
        String checksJson,
        String source,
        LocalDateTime createdAt
) {
}
