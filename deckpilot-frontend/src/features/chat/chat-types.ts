import type { DeckResponse } from "@/features/decks/decks-types";

export type ChatRole = "USER" | "ASSISTANT" | "SYSTEM" | string;

export interface ChatSessionResponse {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChatMessageResponse {
  id: number;
  role: ChatRole;
  content: string;
  createdAt: string;
}

export interface ChatGeneratedDeckResponse {
  id: number;
  sessionId: number;
  generationIndex: number;
  deck: DeckResponse;
  userMessageId: number | null;
  assistantMessageId: number | null;
  createdAt: string;
}

export interface ChatSendMessageResponse {
  userMessage: ChatMessageResponse;
  assistantMessage: ChatMessageResponse;
  generatedDeck: ChatGeneratedDeckResponse | null;
}

export interface ChatSessionCreateRequest {
  title?: string | null;
}

export interface ChatMessageCreateRequest {
  content: string;
}
