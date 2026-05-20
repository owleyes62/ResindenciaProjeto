package com.engage.deckpilot.domain.deck;

import com.engage.deckpilot.domain.card.Card;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "deck_card",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_deck_card_deck_card_section",
                        columnNames = {"deck_id", "card_id", "section"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private Integer copies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeckSection section;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}