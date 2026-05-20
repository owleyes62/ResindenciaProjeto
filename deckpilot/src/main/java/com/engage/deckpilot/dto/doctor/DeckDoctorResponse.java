package com.engage.deckpilot.dto.doctor;

import java.time.LocalDateTime;
import java.util.List;

public record DeckDoctorResponse(
        Long diagnosisId,
        Long deckId,
        String deckName,
        String summary,
        List<String> strengths,
        List<String> risks,
        List<String> suggestions,
        List<DeckDoctorCheckResponse> checks,
        String source,
        LocalDateTime createdAt
) {
}
