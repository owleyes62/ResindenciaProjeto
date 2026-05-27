package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckDiagnosis;
import com.engage.deckpilot.domain.deck.DeckSection;
import com.engage.deckpilot.dto.doctor.DeckDoctorAIResponse;
import com.engage.deckpilot.repository.DeckDiagnosisRepository;
import com.engage.deckpilot.repository.DeckRepository;
import com.engage.deckpilot.service.ai.DeckDoctorPromptBuilder;
import com.engage.deckpilot.service.ai.GroqLlmClient;
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
public class DeckDoctorAIService {

    private final DeckRepository deckRepository;
    private final DeckDiagnosisRepository deckDiagnosisRepository;
    private final ObjectMapper objectMapper;
    private final GroqLlmClient groqLlmClient;
    private final DeckDoctorPromptBuilder deckDoctorPromptBuilder;

    @Transactional
    public DeckDoctorAIResponse analyzeDeckWithAI(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with id: " + deckId));

        int mainCount = countBySection(deck, DeckSection.MAIN);
        int extraCount = countBySection(deck, DeckSection.EXTRA);
        int sideCount = countBySection(deck, DeckSection.SIDE);

        List<String> strengths = new ArrayList<>();
        List<String> risks = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (mainCount == 40) {
            strengths.add("O Main Deck possui 40 cartas, o que tende a melhorar a consistência.");
        }

        if (extraCount == 0) {
            risks.add("O Extra Deck está vazio, o que pode limitar linhas de jogo dependendo da estratégia.");
            suggestions.add("Avaliar se o arquétipo se beneficia de monstros de Extra Deck.");
        }

        if (sideCount == 0) {
            suggestions.add("Adicionar cartas ao Side Deck futuramente para melhorar partidas contra estratégias específicas.");
        }

        if (deck.getWinCondition() != null && !deck.getWinCondition().isBlank()) {
            strengths.add("O deck possui uma condição de vitória descrita.");
        } else {
            risks.add("A condição de vitória não está clara.");
            suggestions.add("Definir melhor como o deck pretende vencer a partida.");
        }

        String summary = "Análise IA simulada para o deck '" + deck.getName() + "'. "
                + "O deck possui " + mainCount + " cartas no Main Deck, "
                + extraCount + " no Extra Deck e "
                + sideCount + " no Side Deck.";

        String aiCommentary = groqLlmClient.chatForDoctor(
                deckDoctorPromptBuilder.systemPrompt(),
                deckDoctorPromptBuilder.userPrompt(deck)
        );

        DeckDiagnosis diagnosis = DeckDiagnosis.builder()
                .deck(deck)
                .summary(summary)
                .strengths(toJson(strengths))
                .risks(toJson(risks))
                .suggestions(toJson(suggestions))
                .checksJson(toJson(List.of(
                        "mainDeckCount=" + mainCount,
                        "extraDeckCount=" + extraCount,
                        "sideDeckCount=" + sideCount
                )))
                .source("ai")
                .build();

        DeckDiagnosis savedDiagnosis = deckDiagnosisRepository.save(diagnosis);

        return new DeckDoctorAIResponse(
                savedDiagnosis.getId(),
                deck.getId(),
                deck.getName(),
                summary,
                strengths,
                risks,
                suggestions,
                aiCommentary,
                savedDiagnosis.getSource(),
                savedDiagnosis.getCreatedAt()
        );
    }

    private int countBySection(Deck deck, DeckSection section) {
        return deck.getCards()
                .stream()
                .filter(deckCard -> deckCard.getSection() == section)
                .mapToInt(deckCard -> deckCard.getCopies() == null ? 0 : deckCard.getCopies())
                .sum();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AI diagnosis data", e);
        }
    }
}
