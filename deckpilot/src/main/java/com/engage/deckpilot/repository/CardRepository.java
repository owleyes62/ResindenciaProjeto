package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByNameIgnoreCase(String name);
    Optional<Card> findByExternalId(Long externalId);
}