package com.engage.deckpilot.service.ai;

import com.engage.deckpilot.domain.deck.Deck;
import com.engage.deckpilot.domain.deck.DeckCard;
import com.engage.deckpilot.domain.deck.DeckSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeckDoctorPromptBuilder {

    private static final String SYSTEM_PROMPT_PATH = "prompts/deck-doctor-system.txt";
    private static final String USER_PROMPT_PATH = "prompts/deck-doctor-user.txt";

    private final PromptLoader promptLoader;

    public String systemPrompt() {
        return promptLoader.load(SYSTEM_PROMPT_PATH);
    }

    public String userPrompt(Deck deck) {
        return promptLoader.render(USER_PROMPT_PATH, Map.of(
                "name", nullSafe(deck.getName()),
                "archetype", nullSafe(deck.getArchetype()),
                "playStyle", nullSafe(deck.getPlayStyle()),
                "format", nullSafe(deck.getFormat()),
                "winCondition", nullSafe(deck.getWinCondition()),
                "howToPilot", nullSafe(deck.getHowToPilot()),
                "mainCards", formatCards(deck, DeckSection.MAIN),
                "extraCards", formatCards(deck, DeckSection.EXTRA),
                "sideCards", formatCards(deck, DeckSection.SIDE)
        ));
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
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
