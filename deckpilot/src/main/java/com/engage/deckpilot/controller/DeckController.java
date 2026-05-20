package com.engage.deckpilot.controller;

import com.engage.deckpilot.dto.deck.DeckCreateRequest;
import com.engage.deckpilot.dto.deck.DeckResponse;
import com.engage.deckpilot.dto.deck.DeckValidationResponse;
import com.engage.deckpilot.service.DeckValidationService;
import com.engage.deckpilot.service.DeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
@Tag(name = "Decks", description = "Endpoints para criação, listagem, busca e validação de decks")
public class DeckController {

    private final DeckService deckService;
    private final DeckValidationService deckValidationService;

    @Operation(summary = "Criar deck", description = "Cria um deck manualmente a partir de uma lista de cartas")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeckResponse createDeck(@RequestBody @Valid DeckCreateRequest request) {
        return deckService.createDeck(request);
    }

    @Operation(summary = "Listar decks", description = "Retorna decks salvos com paginação")
    @GetMapping
    public Page<DeckResponse> listDecks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return deckService.listDecks(page, size);
    }

    @Operation(summary = "Buscar deck por ID", description = "Retorna um deck salvo pelo seu identificador")
    @GetMapping("/{id}")
    public DeckResponse findById(@PathVariable Long id) {
        return deckService.findById(id);
    }

    @Operation(summary = "Validar deck", description = "Valida tamanho do Main Deck, Extra Deck, Side Deck, limite de cópias e existência das cartas")
    @PostMapping("/validate")
    public DeckValidationResponse validateDeck(@RequestBody @Valid DeckCreateRequest request) {
        return deckValidationService.validate(request);
    }
}
