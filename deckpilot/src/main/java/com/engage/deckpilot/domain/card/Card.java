package com.engage.deckpilot.domain.card;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private Long externalId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "card_type")
    private String cardType;

    private String race;

    private String attribute;

    private Integer level;

    private Integer atk;

    @Column(name = "defense")
    private Integer defense;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Column(name = "image_small_url", columnDefinition = "text")
    private String imageSmallUrl;

    @Column(name = "image_cropped_url", columnDefinition = "text")
    private String imageCroppedUrl;

    @Column(nullable = false)
    private String source;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}