package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardNameResolver {

    private final CardRepository cardRepository;

    public Card resolve(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new EntityNotFoundException("AI generated a card with empty name.");
        }

        String cleanedName = cleanCardName(rawName);

        Optional<Card> exactMatch = cardRepository.findByNameIgnoreCase(cleanedName);

        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        List<Card> candidates = cardRepository
                .findByNameContainingIgnoreCase(cleanedName, PageRequest.of(0, 10))
                .getContent();

        if (candidates.isEmpty()) {
            String simplifiedName = simplifyName(cleanedName);

            candidates = cardRepository
                    .findByNameContainingIgnoreCase(simplifiedName, PageRequest.of(0, 10))
                    .getContent();
        }

        return candidates.stream()
                .min(Comparator.comparingInt(card -> distance(
                        normalize(card.getName()),
                        normalize(cleanedName)
                )))
                .orElseThrow(() -> new EntityNotFoundException(
                        "AI generated a card that was not found in database: " + rawName
                ));
    }

    private String cleanCardName(String rawName) {
        return rawName
                .replace("\"", "")
                .replace("'", "")
                .replace("“", "")
                .replace("”", "")
                .trim();
    }

    private String simplifyName(String name) {
        String simplified = name;

        simplified = simplified.replaceAll("(?i)^the\\s+", "");
        simplified = simplified.replaceAll("(?i)\\s+card$", "");
        simplified = simplified.replaceAll("(?i)\\s+monster$", "");
        simplified = simplified.replaceAll("(?i)\\s+spell$", "");
        simplified = simplified.replaceAll("(?i)\\s+trap$", "");

        return simplified.trim();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    private int distance(String left, String right) {
        int[][] dp = new int[left.length() + 1][right.length() + 1];

        for (int i = 0; i <= left.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= right.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= left.length(); i++) {
            for (int j = 1; j <= right.length(); j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[left.length()][right.length()];
    }
}
