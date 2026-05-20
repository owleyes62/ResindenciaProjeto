package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.deck.DeckCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckCardRepository extends JpaRepository<DeckCard, Long> {

    List<DeckCard> findByDeckId(Long deckId);
}