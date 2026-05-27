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
import com.engage.deckpilot.repository.CardRepository;
import com.engage.deckpilot.repository.ChatGeneratedDeckRepository;
import com.engage.deckpilot.repository.DeckRepository;
import com.engage.deckpilot.service.ai.DeckGenerationPromptBuilder;
import com.engage.deckpilot.service.ai.GroqLlmClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiDeckGenerationService {

    private final GroqLlmClient groqLlmClient;
    private final DeckGenerationPromptBuilder deckGenerationPromptBuilder;
    private final ObjectMapper objectMapper;

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final ChatGeneratedDeckRepository chatGeneratedDeckRepository;

    @Transactional
    public GeneratedDeckResult generateDeckFromMessage(
            ChatSession session,
            ChatMessage userMessage,
            ChatMessage assistantMessage
    ) {
        String rawJson = groqLlmClient.chatForDeckGeneration(
                deckGenerationPromptBuilder.systemPrompt(),
                deckGenerationPromptBuilder.userPrompt(userMessage.getContent())
        );

        AiDeckGenerationResponse aiDeck = parseAiResponse(rawJson);

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

            Card card = cardRepository.findByNameIgnoreCase(aiCard.cardName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "AI generated a card that was not found in database: " + aiCard.cardName()
                    ));

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
