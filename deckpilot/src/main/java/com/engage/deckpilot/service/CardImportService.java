package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.dto.card.CardImportResultResponse;
import com.engage.deckpilot.repository.CardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardImportService {

    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CardImportResultResponse importFromFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        JsonNode root = objectMapper.readTree(file);
        JsonNode rawCards = root.has("data") ? root.get("data") : root;

        int created = 0;
        int updated = 0;
        int skipped = 0;
        int total = rawCards.size();

        Iterator<JsonNode> iterator = rawCards.iterator();

        while (iterator.hasNext()) {
            JsonNode rawCard = iterator.next();

            String name = getText(rawCard, "name");
            Long externalId = getLong(rawCard, "id");

            if (name == null || name.isBlank()) {
                skipped++;
                continue;
            }

            Optional<Card> existing = Optional.empty();

            if (externalId != null) {
                existing = cardRepository.findByExternalId(externalId);
            }

            if (existing.isEmpty()) {
                existing = cardRepository.findByNameIgnoreCase(name);
            }

            if (existing.isPresent()) {
                skipped++;
                continue;
            }

            JsonNode firstImage = null;
            JsonNode cardImages = rawCard.get("card_images");
            if (cardImages != null && cardImages.isArray() && !cardImages.isEmpty()) {
                firstImage = cardImages.get(0);
            }

            Card card = Card.builder()
                    .externalId(externalId)
                    .name(name)
                    .cardType(getText(rawCard, "type"))
                    .race(getText(rawCard, "race"))
                    .attribute(getText(rawCard, "attribute"))
                    .level(getInteger(rawCard, "level"))
                    .atk(getInteger(rawCard, "atk"))
                    .defense(getInteger(rawCard, "def"))
                    .description(getText(rawCard, "desc"))
                    .imageUrl(getText(firstImage, "image_url"))
                    .imageSmallUrl(getText(firstImage, "image_url_small"))
                    .imageCroppedUrl(getText(firstImage, "image_url_cropped"))
                    .source("ygoprodeck")
                    .build();

            cardRepository.save(card);
            created++;
        }

        return CardImportResultResponse.builder()
                .created(created)
                .updated(updated)
                .skipped(skipped)
                .total(total)
                .build();
    }

    private String getText(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }

    private Long getLong(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asLong();
    }

    private Integer getInteger(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asInt();
    }
}