package com.engage.deckpilot.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CardImportResultResponse {
    private int created;
    private int updated;
    private int skipped;
    private int total;
}