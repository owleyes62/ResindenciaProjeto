package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.deck.DeckCreateRequest;
import com.engage.deckpilot.dto.deck.DeckResponse;
import com.engage.deckpilot.dto.deck.DeckValidationResponse;
import com.engage.deckpilot.service.DeckValidationService;
import com.engage.deckpilot.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;
    private final DeckValidationService deckValidationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeckResponse createDeck(@RequestBody @Valid DeckCreateRequest request) {
        return deckService.createDeck(request);
    }

    @GetMapping
    public Page<DeckResponse> listDecks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return deckService.listDecks(page, size);
    }

    @GetMapping("/{id}")
    public DeckResponse findById(@PathVariable Long id) {
        return deckService.findById(id);
    }

    @PostMapping("/validate")
    public DeckValidationResponse validateDeck(@RequestBody @Valid DeckCreateRequest request) {
        return deckValidationService.validate(request);
    }
}