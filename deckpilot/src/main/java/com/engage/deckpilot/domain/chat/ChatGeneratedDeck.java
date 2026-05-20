package com.engage.deckpilot.domain.chat;

import com.engage.deckpilot.domain.deck.Deck;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_generated_deck",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_chat_generated_deck_session_index",
                        columnNames = {"session_id", "generation_index"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGeneratedDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "generation_index", nullable = false)
    private Integer generationIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_message_id")
    private ChatMessage userMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_message_id")
    private ChatMessage assistantMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}