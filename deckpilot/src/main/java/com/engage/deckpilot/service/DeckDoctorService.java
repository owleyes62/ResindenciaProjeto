package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckDiagnosis;
import com.engage.deckpilot.domain.deck.DeckSection;
import com.engage.deckpilot.dto.doctor.DeckDiagnosisHistoryResponse;
import com.engage.deckpilot.dto.doctor.DeckDoctorCheckResponse;
import com.engage.deckpilot.dto.doctor.DeckDoctorResponse;
import com.engage.deckpilot.repository.DeckDiagnosisRepository;
import com.engage.deckpilot.repository.DeckRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeckDoctorService {

    private final DeckRepository deckRepository;
    private final DeckDiagnosisRepository deckDiagnosisRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public DeckDoctorResponse analyzeDeck(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        int mainCount = countBySection(deck, DeckSection.MAIN);
        int extraCount = countBySection(deck, DeckSection.EXTRA);
        int sideCount = countBySection(deck, DeckSection.SIDE);

        List<DeckDoctorCheckResponse> checks = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<String> risks = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        checkMainDeckSize(mainCount, checks, strengths, risks, suggestions);
        checkExtraDeckSize(extraCount, checks, strengths, risks, suggestions);
        checkSideDeckSize(sideCount, checks, strengths, risks, suggestions);
        checkDuplicateLimits(deck, checks, risks, suggestions);
        checkDeckMetadata(deck, checks, strengths, risks, suggestions);

        String summary = buildSummary(deck, mainCount, extraCount, sideCount, risks);

        String strengthsJson = toJson(strengths);
        String risksJson = toJson(risks);
        String suggestionsJson = toJson(suggestions);
        String checksJson = toJson(checks);

        DeckDiagnosis diagnosis = DeckDiagnosis.builder()
                .deck(deck)
                .summary(summary)
                .strengths(strengthsJson)
                .risks(risksJson)
                .suggestions(suggestionsJson)
                .checksJson(checksJson)
                .source("local")
                .build();

        DeckDiagnosis savedDiagnosis = deckDiagnosisRepository.save(diagnosis);

        return new DeckDoctorResponse(
                savedDiagnosis.getId(),
                deck.getId(),
                deck.getName(),
                summary,
                strengths,
                risks,
                suggestions,
                checks,
                savedDiagnosis.getSource(),
                savedDiagnosis.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DeckDiagnosisHistoryResponse> listDiagnoses(Long deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new EntityNotFoundException("Deck not found with id: " + deckId);
        }

        return deckDiagnosisRepository.findByDeckIdOrderByCreatedAtDesc(deckId)
                .stream()
                .map(this::toDiagnosisHistoryResponse)
                .toList();
    }

    private DeckDiagnosisHistoryResponse toDiagnosisHistoryResponse(DeckDiagnosis diagnosis) {
        return new DeckDiagnosisHistoryResponse(
                diagnosis.getId(),
                diagnosis.getDeck().getId(),
                diagnosis.getSummary(),
                diagnosis.getSource(),
                diagnosis.getCreatedAt()
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize diagnosis data", e);
        }
    }

    private int countBySection(Deck deck, DeckSection section) {
        return deck.getCards()
                .stream()
                .filter(deckCard -> deckCard.getSection() == section)
                .mapToInt(deckCard -> deckCard.getCopies() == null ? 0 : deckCard.getCopies())
                .sum();
    }

    private void checkMainDeckSize(
            int mainCount,
            List<DeckDoctorCheckResponse> checks,
            List<String> strengths,
            List<String> risks,
            List<String> suggestions
    ) {
        boolean passed = mainCount >= 40 && mainCount <= 60;

        checks.add(new DeckDoctorCheckResponse(
                "Main Deck size",
                passed,
                "Main Deck has " + mainCount + " cards."
        ));

        if (mainCount == 40) {
            strengths.add("Main Deck has exactly 40 cards, which usually improves consistency.");
        } else if (mainCount >= 41 && mainCount <= 60) {
            suggestions.add("Consider reducing the Main Deck closer to 40 cards if consistency is a priority.");
        } else if (mainCount < 40) {
            risks.add("Main Deck has fewer than 40 cards and is not legal for standard play.");
            suggestions.add("Add more cards until the Main Deck has at least 40 cards.");
        } else {
            risks.add("Main Deck has more than 60 cards and is not legal for standard play.");
            suggestions.add("Remove cards until the Main Deck has at most 60 cards.");
        }
    }

    private void checkExtraDeckSize(
            int extraCount,
            List<DeckDoctorCheckResponse> checks,
            List<String> strengths,
            List<String> risks,
            List<String> suggestions
    ) {
        boolean passed = extraCount <= 15;

        checks.add(new DeckDoctorCheckResponse(
                "Extra Deck size",
                passed,
                "Extra Deck has " + extraCount + " cards."
        ));

        if (extraCount > 15) {
            risks.add("Extra Deck has more than 15 cards and is not legal.");
            suggestions.add("Reduce the Extra Deck to 15 cards or fewer.");
        } else if (extraCount == 15) {
            strengths.add("Extra Deck uses the full 15-card limit.");
        } else if (extraCount == 0) {
            suggestions.add("Extra Deck is empty. This may be fine for some strategies, but many decks benefit from Extra Deck options.");
        }
    }

    private void checkSideDeckSize(
            int sideCount,
            List<DeckDoctorCheckResponse> checks,
            List<String> strengths,
            List<String> risks,
            List<String> suggestions
    ) {
        boolean passed = sideCount <= 15;

        checks.add(new DeckDoctorCheckResponse(
                "Side Deck size",
                passed,
                "Side Deck has " + sideCount + " cards."
        ));

        if (sideCount > 15) {
            risks.add("Side Deck has more than 15 cards and is not legal.");
            suggestions.add("Reduce the Side Deck to 15 cards or fewer.");
        } else if (sideCount == 15) {
            strengths.add("Side Deck uses the full 15-card limit.");
        } else if (sideCount == 0) {
            suggestions.add("Side Deck is empty. Add side cards later to improve matchup flexibility.");
        }
    }

    private void checkDuplicateLimits(
            Deck deck,
            List<DeckDoctorCheckResponse> checks,
            List<String> risks,
            List<String> suggestions
    ) {
        List<DeckCard> cardsAboveLimit = deck.getCards()
                .stream()
                .filter(deckCard -> deckCard.getCopies() != null && deckCard.getCopies() > 3)
                .toList();

        boolean passed = cardsAboveLimit.isEmpty();

        checks.add(new DeckDoctorCheckResponse(
                "Copy limit",
                passed,
                passed
                        ? "No card has more than 3 copies in a single section."
                        : "Some cards have more than 3 copies."
        ));

        for (DeckCard deckCard : cardsAboveLimit) {
            String cardName = deckCard.getCard().getName();

            risks.add("Card '" + cardName + "' has more than 3 copies in " + deckCard.getSection() + ".");
            suggestions.add("Reduce '" + cardName + "' to 3 copies or fewer.");
        }
    }

    private void checkDeckMetadata(
            Deck deck,
            List<DeckDoctorCheckResponse> checks,
            List<String> strengths,
            List<String> risks,
            List<String> suggestions
    ) {
        boolean hasArchetype = deck.getArchetype() != null && !deck.getArchetype().isBlank();
        boolean hasPlayStyle = deck.getPlayStyle() != null && !deck.getPlayStyle().isBlank();
        boolean hasWinCondition = deck.getWinCondition() != null && !deck.getWinCondition().isBlank();
        boolean hasHowToPilot = deck.getHowToPilot() != null && !deck.getHowToPilot().isBlank();

        checks.add(new DeckDoctorCheckResponse(
                "Deck identity",
                hasArchetype && hasPlayStyle,
                "Checks whether the deck has archetype and play style information."
        ));

        if (hasArchetype && hasPlayStyle) {
            strengths.add("Deck has a defined archetype and play style.");
        } else {
            risks.add("Deck identity is incomplete.");
            suggestions.add("Define an archetype and play style to make the deck easier to evaluate.");
        }

        checks.add(new DeckDoctorCheckResponse(
                "Pilot guidance",
                hasWinCondition && hasHowToPilot,
                "Checks whether the deck has win condition and pilot instructions."
        ));

        if (hasWinCondition && hasHowToPilot) {
            strengths.add("Deck includes win condition and pilot guidance.");
        } else {
            suggestions.add("Add win condition and how-to-pilot notes to make the strategy clearer.");
        }
    }

    private String buildSummary(
            Deck deck,
            int mainCount,
            int extraCount,
            int sideCount,
            List<String> risks
    ) {
        String base = "Deck '" + deck.getName() + "' has " +
                mainCount + " Main Deck cards, " +
                extraCount + " Extra Deck cards and " +
                sideCount + " Side Deck cards.";

        if (risks.isEmpty()) {
            return base + " No critical structural risks were found.";
        }

        return base + " The analysis found " + risks.size() + " risk(s) that should be reviewed.";
    }
}
