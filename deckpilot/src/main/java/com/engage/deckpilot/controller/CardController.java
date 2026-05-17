package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.card.CardImportResultResponse;
import com.engage.deckpilot.service.CardImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardImportService cardImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importCards() {
        try {
            CardImportResultResponse result = cardImportService.importFromFile("data/ygojson/cards.json");
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Cards imported successfully",
                            "result", result
                    )
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("detail", e.getMessage()));
        }
    }
}