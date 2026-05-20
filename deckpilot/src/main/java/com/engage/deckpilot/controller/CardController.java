package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.card.CardImportResultResponse;
import com.engage.deckpilot.dto.card.CardResponse;
import com.engage.deckpilot.service.CardImportService;
import com.engage.deckpilot.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cards", description = "Endpoints para importação, listagem e busca de cartas")
public class CardController {

    private final CardImportService cardImportService;
    private final CardService cardService;

    @Operation(summary = "Importar cartas", description = "Importa cartas do arquivo local data/ygojson/cards.json")
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

    @Operation(summary = "Listar cartas", description = "Retorna cartas cadastradas com paginação")
    @GetMapping
    public Page<CardResponse> listCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return cardService.listCards(page, size);
    }

    @Operation(summary = "Buscar cartas", description = "Busca cartas pelo nome")
    @GetMapping("/search")
    public Page<CardResponse> searchCards(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return cardService.searchCards(q, limit);
    }
}