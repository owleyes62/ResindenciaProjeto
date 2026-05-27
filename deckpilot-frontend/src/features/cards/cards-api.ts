import { apiClient } from "@/features/api/api-client";
import type { Page } from "@/features/api/page-types";
import type { CardResponse } from "./cards-types";

export async function importCards(): Promise<void> {
  await apiClient.post("/cards/import");
}

export async function listCards(page = 0, size = 20): Promise<Page<CardResponse>> {
  const res = await apiClient.get<Page<CardResponse>>("/cards", {
    params: { page, size },
  });
  return res.data;
}

export async function searchCards(q: string, limit = 10): Promise<CardResponse[]> {
  const res = await apiClient.get<CardResponse[]>("/cards/search", {
    params: { q, limit },
  });
  return res.data;
}
