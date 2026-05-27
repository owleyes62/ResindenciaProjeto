package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.domain.chat.ChatGeneratedDeck;
import com.engage.deckpilot.domain.chat.ChatMessage;
import com.engage.deckpilot.domain.chat.ChatSession;
import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckSection;
import com.engage.deckpilot.dto.ai.AiDeckCardResponse;
import com.engage.deckpilot.dto.ai.AiDeckGenerationResponse;
import com.engage.deckpilot.repository.ChatGeneratedDeckRepository;
import com.engage.deckpilot.repository.DeckRepository;
import com.engage.deckpilot.service.ai.DeckGenerationPromptBuilder;
import com.engage.deckpilot.service.ai.GroqLlmClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiDeckGenerationService {

    private final GroqLlmClient groqLlmClient;
    private final DeckGenerationPromptBuilder deckGenerationPromptBuilder;
    private final ObjectMapper objectMapper;

    private final CardNameResolver cardNameResolver;
    private final DeckRepository deckRepository;
    private final ChatGeneratedDeckRepository chatGeneratedDeckRepository;

    private static final int MAX_GENERATION_ATTEMPTS = 3;

    @Transactional
    public GeneratedDeckResult generateDeckFromMessage(
            ChatSession session,
            ChatMessage userMessage,
            ChatMessage assistantMessage
    ) {
        AiDeckGenerationResponse aiDeck = generateValidatedDeck(userMessage.getContent());

        Deck deck = Deck.builder()
                .name(aiDeck.name())
                .archetype(aiDeck.archetype())
                .playStyle(aiDeck.playStyle())
                .format(aiDeck.format())
                .winCondition(aiDeck.winCondition())
                .howToPilot(aiDeck.howToPilot())
                .source("ai")
                .build();

        addCards(deck, aiDeck.mainDeck(), DeckSection.MAIN);
        addCards(deck, aiDeck.extraDeck(), DeckSection.EXTRA);
        addCards(deck, aiDeck.sideDeck(), DeckSection.SIDE);

        Deck savedDeck = deckRepository.save(deck);

        int nextGenerationIndex = chatGeneratedDeckRepository.countBySessionId(session.getId()) + 1;

        ChatGeneratedDeck generatedDeck = ChatGeneratedDeck.builder()
                .session(session)
                .deck(savedDeck)
                .generationIndex(nextGenerationIndex)
                .userMessage(userMessage)
                .assistantMessage(assistantMessage)
                .build();

        ChatGeneratedDeck savedGeneratedDeck = chatGeneratedDeckRepository.save(generatedDeck);

        String assistantContent = aiDeck.assistantMessage();

        if (assistantContent == null || assistantContent.isBlank()) {
            assistantContent = "Gerei um deck com base no seu pedido.";
        }

        return new GeneratedDeckResult(savedGeneratedDeck, assistantContent);
    }

    private AiDeckGenerationResponse generateValidatedDeck(String userContent) {
        String systemPrompt = deckGenerationPromptBuilder.systemPrompt();
        String baseUserPrompt = deckGenerationPromptBuilder.userPrompt(userContent);

        String currentUserPrompt = baseUserPrompt;
        IllegalArgumentException lastError = null;

        for (int attempt = 1; attempt <= MAX_GENERATION_ATTEMPTS; attempt++) {
            String rawJson = groqLlmClient.chatForDeckGeneration(systemPrompt, currentUserPrompt);
            AiDeckGenerationResponse aiDeck = parseAiResponse(rawJson);
            aiDeck = capCrossSectionCopies(aiDeck);
            aiDeck = padMainDeckIfUndersized(aiDeck);

            try {
                validateAiDeckStructure(aiDeck);
                return aiDeck;
            } catch (IllegalArgumentException e) {
                lastError = e;
                currentUserPrompt = baseUserPrompt
                        + "\n\nSua tentativa anterior falhou com este erro:\n"
                        + e.getMessage()
                        + "\n\nCorrija o problema e devolva um JSON válido respeitando todas as regras de tamanho e cópias.";
            }
        }

        throw lastError != null
                ? lastError
                : new IllegalStateException("AI deck generation failed without a specific error.");
    }

    private AiDeckGenerationResponse capCrossSectionCopies(AiDeckGenerationResponse aiDeck) {
        Map<String, Integer> totals = new HashMap<>();
        addCopiesToMap(aiDeck.mainDeck(), totals);
        addCopiesToMap(aiDeck.extraDeck(), totals);
        addCopiesToMap(aiDeck.sideDeck(), totals);

        boolean needsTrim = totals.values().stream().anyMatch(value -> value > 3);

        if (!needsTrim) {
            return aiDeck;
        }

        Map<String, Integer> kept = new HashMap<>();

        List<AiDeckCardResponse> mainTrimmed = trimSection(aiDeck.mainDeck(), kept);
        List<AiDeckCardResponse> extraTrimmed = trimSection(aiDeck.extraDeck(), kept);
        List<AiDeckCardResponse> sideTrimmed = trimSection(aiDeck.sideDeck(), kept);

        return new AiDeckGenerationResponse(
                aiDeck.name(),
                aiDeck.archetype(),
                aiDeck.playStyle(),
                aiDeck.format(),
                aiDeck.mainDeckCount(),
                aiDeck.extraDeckCount(),
                aiDeck.sideDeckCount(),
                aiDeck.winCondition(),
                aiDeck.howToPilot(),
                mainTrimmed,
                extraTrimmed,
                sideTrimmed,
                aiDeck.assistantMessage()
        );
    }

    private List<AiDeckCardResponse> trimSection(
            List<AiDeckCardResponse> section,
            Map<String, Integer> kept
    ) {
        if (section == null || section.isEmpty()) {
            return section;
        }

        List<AiDeckCardResponse> result = new ArrayList<>();

        for (AiDeckCardResponse card : section) {
            if (card == null || card.cardName() == null) {
                continue;
            }

            String key = card.cardName().trim().toLowerCase();
            int alreadyKept = kept.getOrDefault(key, 0);
            int budget = Math.max(0, 3 - alreadyKept);
            int wantedCopies = card.copies() == null ? 0 : card.copies();
            int allowedHere = Math.min(wantedCopies, budget);

            if (allowedHere > 0) {
                result.add(new AiDeckCardResponse(card.cardName(), allowedHere));
                kept.put(key, alreadyKept + allowedHere);
            }
        }

        return result;
    }

    private AiDeckGenerationResponse padMainDeckIfUndersized(AiDeckGenerationResponse aiDeck) {
        List<AiDeckCardResponse> mainDeck = aiDeck.mainDeck();

        if (mainDeck == null || mainDeck.isEmpty()) {
            return aiDeck;
        }

        int mainCount = countCards(mainDeck);

        if (mainCount >= 40) {
            return aiDeck;
        }

        Map<String, Integer> totalsAcrossSections = new HashMap<>();
        addCopiesToMap(aiDeck.mainDeck(), totalsAcrossSections);
        addCopiesToMap(aiDeck.extraDeck(), totalsAcrossSections);
        addCopiesToMap(aiDeck.sideDeck(), totalsAcrossSections);

        List<AiDeckCardResponse> padded = new ArrayList<>(mainDeck);
        int safetyBound = padded.size() * 3;
        int iterations = 0;

        while (mainCount < 40 && iterations < safetyBound) {
            int idx = iterations % padded.size();
            AiDeckCardResponse card = padded.get(idx);

            if (card != null && card.cardName() != null) {
                String key = card.cardName().trim().toLowerCase();
                int totalCurrent = totalsAcrossSections.getOrDefault(key, 0);

                if (totalCurrent < 3) {
                    int newCopies = (card.copies() == null ? 1 : card.copies()) + 1;
                    padded.set(idx, new AiDeckCardResponse(card.cardName(), newCopies));
                    totalsAcrossSections.put(key, totalCurrent + 1);
                    mainCount++;
                }
            }

            iterations++;
        }

        return new AiDeckGenerationResponse(
                aiDeck.name(),
                aiDeck.archetype(),
                aiDeck.playStyle(),
                aiDeck.format(),
                mainCount,
                aiDeck.extraDeckCount(),
                aiDeck.sideDeckCount(),
                aiDeck.winCondition(),
                aiDeck.howToPilot(),
                padded,
                aiDeck.extraDeck(),
                aiDeck.sideDeck(),
                aiDeck.assistantMessage()
        );
    }

    private void validateAiDeckStructure(AiDeckGenerationResponse aiDeck) {
        int mainDeckCount = countCards(aiDeck.mainDeck());
        int extraDeckCount = countCards(aiDeck.extraDeck());
        int sideDeckCount = countCards(aiDeck.sideDeck());

        List<String> errors = new ArrayList<>();

        if (mainDeckCount < 40) {
            errors.add("Main Deck must have at least 40 cards. Current: " + mainDeckCount);
        }

        if (mainDeckCount > 60) {
            errors.add("Main Deck must have at most 60 cards. Current: " + mainDeckCount);
        }

        if (extraDeckCount > 15) {
            errors.add("Extra Deck must have at most 15 cards. Current: " + extraDeckCount);
        }

        if (sideDeckCount > 15) {
            errors.add("Side Deck must have at most 15 cards. Current: " + sideDeckCount);
        }

        validateMaxCopies(aiDeck.mainDeck(), "MAIN", errors);
        validateMaxCopies(aiDeck.extraDeck(), "EXTRA", errors);
        validateMaxCopies(aiDeck.sideDeck(), "SIDE", errors);
        validateTotalCopiesAcrossSections(aiDeck, errors);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    "AI generated an invalid deck structure: " + String.join("; ", errors)
            );
        }
    }

    private int countCards(List<AiDeckCardResponse> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }

        return cards.stream()
                .filter(Objects::nonNull)
                .mapToInt(card -> card.copies() == null ? 0 : card.copies())
                .sum();
    }

    private void validateMaxCopies(
            List<AiDeckCardResponse> cards,
            String section,
            List<String> errors
    ) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        for (AiDeckCardResponse card : cards) {
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

    private void validateTotalCopiesAcrossSections(
            AiDeckGenerationResponse aiDeck,
            List<String> errors
    ) {
        Map<String, Integer> copiesByCard = new HashMap<>();

        addCopiesToMap(aiDeck.mainDeck(), copiesByCard);
        addCopiesToMap(aiDeck.extraDeck(), copiesByCard);
        addCopiesToMap(aiDeck.sideDeck(), copiesByCard);

        for (Map.Entry<String, Integer> entry : copiesByCard.entrySet()) {
            if (entry.getValue() > 3) {
                errors.add("Card '" + entry.getKey() + "' has more than 3 copies across Main, Extra and Side. Current: " + entry.getValue());
            }
        }
    }

    private void addCopiesToMap(
            List<AiDeckCardResponse> cards,
            Map<String, Integer> copiesByCard
    ) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        for (AiDeckCardResponse card : cards) {
            if (card == null || card.cardName() == null || card.copies() == null) {
                continue;
            }

            String normalizedName = card.cardName().trim().toLowerCase();

            copiesByCard.merge(normalizedName, card.copies(), Integer::sum);
        }
    }

    private AiDeckGenerationResponse parseAiResponse(String rawJson) {
        try {
            String cleanedJson = cleanJson(rawJson);
            return objectMapper.readValue(cleanedJson, AiDeckGenerationResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse AI deck generation response: " + rawJson, e);
        }
    }

    private String cleanJson(String rawText) {
        if (rawText == null) {
            throw new IllegalStateException("AI returned empty response.");
        }

        String text = rawText.trim();

        if (text.startsWith("```json")) {
            text = text.substring(7);
        }

        if (text.startsWith("```")) {
            text = text.substring(3);
        }

        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }

        return text.trim();
    }

    private void addCards(
            Deck deck,
            List<AiDeckCardResponse> cards,
            DeckSection section
    ) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        for (AiDeckCardResponse aiCard : cards) {
            if (aiCard == null || aiCard.cardName() == null || aiCard.cardName().isBlank()) {
                continue;
            }

            Card card = cardNameResolver.resolve(aiCard.cardName());

            DeckCard deckCard = DeckCard.builder()
                    .deck(deck)
                    .card(card)
                    .copies(aiCard.copies() == null ? 1 : aiCard.copies())
                    .section(section)
                    .build();

            deck.getCards().add(deckCard);
        }
    }

    public record GeneratedDeckResult(
            ChatGeneratedDeck generatedDeck,
            String assistantMessage
    ) {
    }
}
