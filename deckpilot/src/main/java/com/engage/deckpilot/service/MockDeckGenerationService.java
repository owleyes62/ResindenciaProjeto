package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.domain.chat.ChatGeneratedDeck;
import com.engage.deckpilot.domain.chat.ChatMessage;
import com.engage.deckpilot.domain.chat.ChatSession;
import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckSection;
import com.engage.deckpilot.repository.CardRepository;
import com.engage.deckpilot.repository.ChatGeneratedDeckRepository;
import com.engage.deckpilot.repository.DeckRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockDeckGenerationService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final ChatGeneratedDeckRepository chatGeneratedDeckRepository;

    @Transactional
    public ChatGeneratedDeck generateMockDeck(
            ChatSession session,
            ChatMessage userMessage,
            ChatMessage assistantMessage
    ) {
        List<Card> cards = cardRepository.findAll(PageRequest.of(0, 40)).getContent();

        if (cards.size() < 40) {
            throw new EntityNotFoundException(
                    "Not enough cards in database to generate a mock deck. Import cards first."
            );
        }

        Deck deck = Deck.builder()
                .name("Deck Mockado")
                .archetype("Mock")
                .playStyle("control")
                .format("TCG")
                .winCondition("Deck criado automaticamente para testar o fluxo de geração.")
                .howToPilot("Use este deck apenas para validar o fluxo chat -> deck -> generated deck.")
                .source("mock")
                .cards(new ArrayList<>())
                .build();

        for (Card card : cards) {
            DeckCard deckCard = DeckCard.builder()
                    .deck(deck)
                    .card(card)
                    .copies(1)
                    .section(DeckSection.MAIN)
                    .build();

            deck.getCards().add(deckCard);
        }

        Deck savedDeck = deckRepository.save(deck);

        int nextGenerationIndex = chatGeneratedDeckRepository.countBySessionId(session.getId()) + 1;

        ChatGeneratedDeck generatedDeck = ChatGeneratedDeck.builder()
                .session(session)
                .deck(savedDeck)
                .generationIndex(nextGenerationIndex)
                .userMessage(userMessage)
                .assistantMessage(assistantMessage)
                .build();

        return chatGeneratedDeckRepository.save(generatedDeck);
    }
}
