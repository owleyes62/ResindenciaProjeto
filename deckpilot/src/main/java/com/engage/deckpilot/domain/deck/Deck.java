package com.engage.deckpilot.domain.deck;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deck")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 100)
    private String archetype;

    @Column(name = "play_style", nullable = false, length = 50)
    private String playStyle;

    @Column(nullable = false, length = 50)
    private String format;

    @Column(name = "win_condition", columnDefinition = "TEXT")
    private String winCondition;

    @Column(name = "how_to_pilot", columnDefinition = "TEXT")
    private String howToPilot;

    @Column(nullable = false, length = 30)
    private String source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeckCard> cards = new ArrayList<>();

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeckDiagnosis> diagnoses = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.format == null || this.format.isBlank()) {
            this.format = "TCG";
        }

        if (this.source == null || this.source.isBlank()) {
            this.source = "manual";
        }

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}