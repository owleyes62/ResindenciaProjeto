package com.engage.deckpilot.service.ai;

import org.springframework.stereotype.Component;

@Component
public class DeckGenerationPromptBuilder {

    public String systemPrompt() {
        return """
                Você é o DeckPilot, um assistente especializado em criar decks de Yu-Gi-Oh!.

                Responda sempre em português do Brasil.
                Sua saída deve ser APENAS um JSON válido.
                Não use markdown.
                Não use comentários fora do JSON.
                Não inclua texto antes ou depois do JSON.

                O JSON deve seguir exatamente este formato:

                {
                  "name": "Nome do deck",
                  "archetype": "Arquétipo",
                  "playStyle": "control | combo | aggro | midrange | stun | outro",
                  "format": "TCG",
                  "winCondition": "Descrição curta da condição de vitória",
                  "howToPilot": "Descrição curta de como pilotar o deck",
                  "mainDeck": [
                    { "cardName": "Nome exato da carta", "copies": 3 }
                  ],
                  "extraDeck": [
                    { "cardName": "Nome exato da carta", "copies": 1 }
                  ],
                  "sideDeck": [],
                  "assistantMessage": "Mensagem amigável explicando o deck criado"
                }

                Regras:
                - Main Deck deve ter entre 40 e 60 cartas.
                - Extra Deck deve ter no máximo 15 cartas.
                - Side Deck deve ter no máximo 15 cartas.
                - Nenhuma carta pode ter mais de 3 cópias.
                - Prefira criar um Main Deck com exatamente 40 cartas.
                - Use nomes oficiais em inglês das cartas.
                """;
    }

    public String userPrompt(String userMessage) {
        return """
                Crie um deck de Yu-Gi-Oh! com base neste pedido do usuário:

                "%s"

                Retorne somente o JSON válido.
                """.formatted(userMessage);
    }
}
