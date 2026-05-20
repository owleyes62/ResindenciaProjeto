package com.engage.deckpilot.service;

import com.engage.deckpilot.dto.deck.DeckCardRequest;
import com.engage.deckpilot.dto.deck.DeckCreateRequest;
import com.engage.deckpilot.dto.deck.DeckValidationResponse;
import com.engage.deckpilot.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeckValidationService {

    private final CardRepository cardRepository;

    public DeckValidationResponse validate(DeckCreateRequest request) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        List<DeckCardRequest> mainDeck = safeList(request.mainDeck());
        List<DeckCardRequest> extraDeck = safeList(request.extraDeck());
        List<DeckCardRequest> sideDeck = safeList(request.sideDeck());

        int mainDeckCount = countCards(mainDeck);
        int extraDeckCount = countCards(extraDeck);
        int sideDeckCount = countCards(sideDeck);

        validateMainDeckSize(mainDeckCount, errors);
        validateExtraDeckSize(extraDeckCount, errors);
        validateSideDeckSize(sideDeckCount, errors);

        validateCopiesPerSection(mainDeck, "MAIN", errors);
        validateCopiesPerSection(extraDeck, "EXTRA", errors);
        validateCopiesPerSection(sideDeck, "SIDE", errors);

        validateTotalCopiesAcrossDeck(mainDeck, extraDeck, sideDeck, errors);

        validateCardsExist(mainDeck, "MAIN", errors);
        validateCardsExist(extraDeck, "EXTRA", errors);
        validateCardsExist(sideDeck, "SIDE", errors);

        addWarnings(mainDeckCount, extraDeckCount, sideDeckCount, warnings);

        return new DeckValidationResponse(
                errors.isEmpty(),
                mainDeckCount,
                extraDeckCount,
                sideDeckCount,
                errors,
                warnings
        );
    }

    private List<DeckCardRequest> safeList(List<DeckCardRequest> cards) {
        return cards == null ? List.of() : cards;
    }

    private int countCards(List<DeckCardRequest> cards) {
        return cards.stream()
                .filter(Objects::nonNull)
                .mapToInt(card -> card.copies() == null ? 0 : card.copies())
                .sum();
    }

    private void validateMainDeckSize(int count, List<String> errors) {
        if (count < 40) {
            errors.add("Main Deck must have at least 40 cards.");
        }

        if (count > 60) {
            errors.add("Main Deck must have at most 60 cards.");
        }
    }

    private void validateExtraDeckSize(int count, List<String> errors) {
        if (count > 15) {
            errors.add("Extra Deck must have at most 15 cards.");
        }
    }

    private void validateSideDeckSize(int count, List<String> errors) {
        if (count > 15) {
            errors.add("Side Deck must have at most 15 cards.");
        }
    }

    private void validateCopiesPerSection(
            List<DeckCardRequest> cards,
            String section,
            List<String> errors
    ) {
        for (DeckCardRequest card : cards) {
            if (card == null) {
                continue;
            }

            if (card.copies() == null || card.copies() < 1) {
                errors.add("Card '" + card.cardName() + "' in " + section + " must have at least 1 copy.");
            }

            if (card.copies() != null && card.copies() > 3) {
                errors.add("Card '" + card.cardName() + "' in " + section + " has more than 3 copies.");
            }
        }
    }

    private void validateTotalCopiesAcrossDeck(
            List<DeckCardRequest> mainDeck,
            List<DeckCardRequest> extraDeck,
            List<DeckCardRequest> sideDeck,
            List<String> errors
    ) {
        Map<String, Integer> copiesByCardName = new HashMap<>();

        addCopiesToMap(mainDeck, copiesByCardName);
        addCopiesToMap(extraDeck, copiesByCardName);
        addCopiesToMap(sideDeck, copiesByCardName);

        for (Map.Entry<String, Integer> entry : copiesByCardName.entrySet()) {
            if (entry.getValue() > 3) {
                errors.add("Card '" + entry.getKey() + "' has more than 3 copies across the deck.");
            }
        }
    }

    private void addCopiesToMap(
            List<DeckCardRequest> cards,
            Map<String, Integer> copiesByCardName
    ) {
        for (DeckCardRequest card : cards) {
            if (card == null || card.cardName() == null || card.copies() == null) {
                continue;
            }

            String normalizedName = card.cardName().trim().toLowerCase();

            copiesByCardName.merge(normalizedName, card.copies(), Integer::sum);
        }
    }

    private void validateCardsExist(
            List<DeckCardRequest> cards,
            String section,
            List<String> errors
    ) {
        for (DeckCardRequest card : cards) {
            if (card == null || card.cardName() == null || card.cardName().isBlank()) {
                errors.add("A card in " + section + " has no name.");
                continue;
            }

            boolean exists = cardRepository.findByNameIgnoreCase(card.cardName()).isPresent();

            if (!exists) {
                errors.add("Card '" + card.cardName() + "' in " + section + " was not found in the database.");
            }
        }
    }

    private void addWarnings(
            int mainDeckCount,
            int extraDeckCount,
            int sideDeckCount,
            List<String> warnings
    ) {
        if (mainDeckCount == 40) {
            warnings.add("Main Deck has exactly 40 cards, which is usually good for consistency.");
        }

        if (extraDeckCount == 0) {
            warnings.add("Extra Deck is empty.");
        }

        if (sideDeckCount == 0) {
            warnings.add("Side Deck is empty.");
        }
    }
}