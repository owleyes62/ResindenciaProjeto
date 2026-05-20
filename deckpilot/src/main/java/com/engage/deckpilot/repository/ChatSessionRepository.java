package com.engage.deckpilot.repository;

import com.engage.deckpilot.domain.chat.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}
