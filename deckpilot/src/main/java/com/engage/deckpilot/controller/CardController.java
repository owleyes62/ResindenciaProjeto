package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.card.CardImportResultResponse;
import com.engage.deckpilot.dto.card.CardResponse;
import com.engage.deckpilot.service.CardImportService;
import com.engage.deckpilot.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final CardService cardService;

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

     @GetMapping
    public Page<CardResponse> listCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return cardService.listCards(page, size);
    }

    @GetMapping("/search")
    public Page<CardResponse> searchCards(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return cardService.searchCards(q, limit);
    }
}