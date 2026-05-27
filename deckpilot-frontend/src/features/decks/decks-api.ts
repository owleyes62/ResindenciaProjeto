import { apiClient } from "@/features/api/api-client";
import type { Page } from "@/features/api/page-types";
import type {
  DeckCreateRequest,
  DeckResponse,
  DeckValidationResponse,
} from "./decks-types";

export async function listDecks(page = 0, size = 20): Promise<Page<DeckResponse>> {
  const res = await apiClient.get<Page<DeckResponse>>("/decks", {
    params: { page, size },
  });
  return res.data;
}

export async function getDeckById(id: number | string): Promise<DeckResponse> {
  const res = await apiClient.get<DeckResponse>(`/decks/${id}`);
  return res.data;
}

export async function createDeck(payload: DeckCreateRequest): Promise<DeckResponse> {
  const res = await apiClient.post<DeckResponse>("/decks", payload);
  return res.data;
}

export async function validateDeck(
  payload: DeckCreateRequest,
): Promise<DeckValidationResponse> {
  const res = await apiClient.post<DeckValidationResponse>("/decks/validate", payload);
  return res.data;
}
