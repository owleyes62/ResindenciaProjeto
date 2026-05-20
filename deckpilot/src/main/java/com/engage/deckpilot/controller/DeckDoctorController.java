package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.doctor.DeckDoctorResponse;
import com.engage.deckpilot.service.DeckDoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "Deck Doctor", description = "Endpoints para análise estrutural de decks")
public class DeckDoctorController {

    private final DeckDoctorService deckDoctorService;

    @Operation(
            summary = "Analisar deck",
            description = "Executa uma análise estrutural local de um deck salvo"
    )
    @GetMapping("/decks/{deckId}")
    public DeckDoctorResponse analyzeDeck(@PathVariable Long deckId) {
        return deckDoctorService.analyzeDeck(deckId);
    }
}
