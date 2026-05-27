export type DeckSection = "MAIN" | "EXTRA" | "SIDE" | string;

export interface DeckCardResponse {
  cardId: number;
  cardName: string;
  copies: number;
  section: DeckSection;
}

export interface DeckResponse {
  id: number;
  name: string;
  archetype: string;
  playStyle: string;
  format: string;
  winCondition: string | null;
  howToPilot: string | null;
  source: string;
  mainDeck: DeckCardResponse[];
  extraDeck: DeckCardResponse[];
  sideDeck: DeckCardResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface DeckCardInput {
  cardName: string;
  copies: number;
}

export interface DeckCreateRequest {
  name: string;
  archetype: string;
  playStyle: string;
  format?: string;
  winCondition?: string;
  howToPilot?: string;
  mainDeck: DeckCardInput[];
  extraDeck: DeckCardInput[];
  sideDeck: DeckCardInput[];
}

export interface DeckValidationResponse {
  valid: boolean;
  mainDeckCount: number;
  extraDeckCount: number;
  sideDeckCount: number;
  errors: string[];
  warnings: string[];
}
