package com.engage.deckpilot.dto.card;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardResponse {
    private Long id;
    private Long externalId;
    private String name;
    private String cardType;
    private String race;
    private String attribute;
    private Integer level;
    private Integer atk;
    private Integer defense;
    private String description;
    private String imageUrl;
    private String imageSmallUrl;
    private String imageCroppedUrl;
    private String source;
}