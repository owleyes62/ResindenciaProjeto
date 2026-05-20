package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.deck.DeckDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckDiagnosisRepository extends JpaRepository<DeckDiagnosis, Long> {

    List<DeckDiagnosis> findByDeckIdOrderByCreatedAtDesc(Long deckId);
}
