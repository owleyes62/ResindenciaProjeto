package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.deck.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    
}