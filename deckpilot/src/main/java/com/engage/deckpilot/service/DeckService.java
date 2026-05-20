package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckSection;
import com.engage.deckpilot.dto.deck.DeckCardRequest;
import com.engage.deckpilot.dto.deck.DeckCardResponse;
import com.engage.deckpilot.dto.deck.DeckCreateRequest;
import com.engage.deckpilot.dto.deck.DeckResponse;
import com.engage.deckpilot.dto.deck.DeckValidationResponse;
import com.engage.deckpilot.repository.CardRepository;
import com.engage.deckpilot.repository.DeckRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final DeckValidationService deckValidationService;

    @Transactional
    public DeckResponse createDeck(DeckCreateRequest request) {
        DeckValidationResponse validation = deckValidationService.validate(request);

        if (!validation.valid()) {
            throw new IllegalArgumentException("Invalid deck: " + String.join("; ", validation.errors()));
        }
        Deck deck = Deck.builder()
                .name(request.name())
                .archetype(request.archetype())
                .playStyle(request.playStyle())
                .format(request.format())
                .winCondition(request.winCondition())
                .howToPilot(request.howToPilot())
                .source("manual")
                .cards(new ArrayList<>())
                .build();

        addCardsToDeck(deck, request.mainDeck(), DeckSection.MAIN);
        addCardsToDeck(deck, request.extraDeck(), DeckSection.EXTRA);
        addCardsToDeck(deck, request.sideDeck(), DeckSection.SIDE);

        Deck savedDeck = deckRepository.save(deck);

        return toResponse(savedDeck);
    }

    @Transactional(readOnly = true)
    public Page<DeckResponse> listDecks(int page, int size) {
        return deckRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DeckResponse findById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + id));

        return toResponse(deck);
    }

    private void addCardsToDeck(Deck deck, List<DeckCardRequest> cards, DeckSection section) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        for (DeckCardRequest cardRequest : cards) {
            Card card = cardRepository.findByNameIgnoreCase(cardRequest.cardName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Card not found: " + cardRequest.cardName()
                    ));

            DeckCard deckCard = DeckCard.builder()
                    .deck(deck)
                    .card(card)
                    .copies(cardRequest.copies())
                    .section(section)
                    .build();

            deck.getCards().add(deckCard);
        }
    }

    public DeckResponse toResponse(Deck deck) {
        List<DeckCardResponse> mainDeck = new ArrayList<>();
        List<DeckCardResponse> extraDeck = new ArrayList<>();
        List<DeckCardResponse> sideDeck = new ArrayList<>();

        for (DeckCard deckCard : deck.getCards()) {
            DeckCardResponse cardResponse = toCardResponse(deckCard);

            if (deckCard.getSection() == DeckSection.MAIN) {
                mainDeck.add(cardResponse);
            } else if (deckCard.getSection() == DeckSection.EXTRA) {
                extraDeck.add(cardResponse);
            } else if (deckCard.getSection() == DeckSection.SIDE) {
                sideDeck.add(cardResponse);
            }
        }

        return new DeckResponse(
                deck.getId(),
                deck.getName(),
                deck.getArchetype(),
                deck.getPlayStyle(),
                deck.getFormat(),
                deck.getWinCondition(),
                deck.getHowToPilot(),
                deck.getSource(),
                mainDeck,
                extraDeck,
                sideDeck,
                deck.getCreatedAt(),
                deck.getUpdatedAt()
        );
    }

    private DeckCardResponse toCardResponse(DeckCard deckCard) {
        return new DeckCardResponse(
                deckCard.getCard().getId(),
                deckCard.getCard().getName(),
                deckCard.getCopies(),
                deckCard.getSection().name()
        );
    }
}