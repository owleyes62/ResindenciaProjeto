package com.engage.deckpilot.dto.doctor;

public record DeckDoctorCheckResponse(
        String name,
        boolean passed,
        String message
) {
}
