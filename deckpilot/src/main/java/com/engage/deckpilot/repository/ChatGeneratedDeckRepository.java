package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.chat.ChatGeneratedDeck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatGeneratedDeckRepository extends JpaRepository<ChatGeneratedDeck, Long> {

    List<ChatGeneratedDeck> findBySessionIdOrderByGenerationIndexAsc(Long sessionId);

    Optional<ChatGeneratedDeck> findBySessionIdAndGenerationIndex(Long sessionId, Integer generationIndex);

    int countBySessionId(Long sessionId);
}
