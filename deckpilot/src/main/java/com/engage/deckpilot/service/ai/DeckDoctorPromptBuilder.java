package com.engage.deckpilot.service.ai;

import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckSection;
import org.springframework.stereotype.Component;

@Component
public class DeckDoctorPromptBuilder {

    public String systemPrompt() {
        return """
                Você é o Deck Doctor do DeckPilot, um avaliador estratégico de decks de Yu-Gi-Oh!.
                Analise decks de forma objetiva, prática e útil para jogadores iniciantes e intermediários.

                Responda sempre em português do Brasil.
                Não invente cartas que não estejam no deck.
                Não altere regras oficiais sem avisar.
                Foque em consistência, plano de jogo, riscos, pontos fortes e sugestões práticas.
                """;
    }

    public String userPrompt(Deck deck) {
        return """
                Analise o seguinte deck de Yu-Gi-Oh!.

                Nome: %s
                Arquétipo: %s
                Estilo de jogo: %s
                Formato: %s
                Condição de vitória: %s
                Como pilotar: %s

                Main Deck:
                %s

                Extra Deck:
                %s

                Side Deck:
                %s

                Retorne a análise no seguinte formato:

                Resumo:
                Pontos fortes:
                Riscos:
                Sugestões:
                Comentário estratégico:
                """.formatted(
                deck.getName(),
                deck.getArchetype(),
                deck.getPlayStyle(),
                deck.getFormat(),
                deck.getWinCondition(),
                deck.getHowToPilot(),
                formatCards(deck, DeckSection.MAIN),
                formatCards(deck, DeckSection.EXTRA),
                formatCards(deck, DeckSection.SIDE)
        );
    }

    private String formatCards(Deck deck, DeckSection section) {
        StringBuilder builder = new StringBuilder();

        for (DeckCard deckCard : deck.getCards()) {
            if (deckCard.getSection() == section) {
                builder.append("- ")
                        .append(deckCard.getCopies())
                        .append("x ")
                        .append(deckCard.getCard().getName())
                        .append("\n");
            }
        }

        if (builder.isEmpty()) {
            return "(vazio)";
        }

        return builder.toString();
    }
}
