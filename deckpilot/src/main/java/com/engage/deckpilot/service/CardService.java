package com.engage.deckpilot.service;

import com.engage.deckpilot.domain.card.Card;
import com.engage.deckpilot.dto.card.CardResponse;
import com.engage.deckpilot.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Page<CardResponse> listCards(int page, int size) {
        return cardRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    public Page<CardResponse> searchCards(String query, int limit) {
        return cardRepository
                .findByNameContainingIgnoreCase(query, PageRequest.of(0, limit))
                .map(this::toResponse);
    }

    private CardResponse toResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .externalId(card.getExternalId())
                .name(card.getName())
                .cardType(card.getCardType())
                .race(card.getRace())
                .attribute(card.getAttribute())
                .level(card.getLevel())
                .atk(card.getAtk())
                .defense(card.getDefense())
                .description(card.getDescription())
                .imageUrl(card.getImageUrl())
                .imageSmallUrl(card.getImageSmallUrl())
                .imageCroppedUrl(card.getImageCroppedUrl())
                .source(card.getSource())
                .build();
    }
}