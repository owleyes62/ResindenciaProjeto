package com.engage.deckpilot.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeckGenerationPromptBuilder {

    private static final String SYSTEM_PROMPT_PATH = "prompts/deck-generation-system.txt";
    private static final String USER_PROMPT_PATH = "prompts/deck-generation-user.txt";

    private final PromptLoader promptLoader;

    public String systemPrompt() {
        return promptLoader.load(SYSTEM_PROMPT_PATH);
    }

    public String userPrompt(String userMessage) {
        return promptLoader.render(USER_PROMPT_PATH, Map.of(
                "userMessage", userMessage == null ? "" : userMessage
        ));
    }
}
