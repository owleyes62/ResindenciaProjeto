import { apiClient } from "@/features/api/api-client";
import type {
  DeckDiagnosisDetailResponse,
  DeckDiagnosisHistoryResponse,
  DeckDoctorAIResponse,
  DeckDoctorResponse,
} from "./doctor-types";

export async function analyzeDeckLocal(
  deckId: number | string,
): Promise<DeckDoctorResponse> {
  const res = await apiClient.get<DeckDoctorResponse>(`/doctor/decks/${deckId}`);
  return res.data;
}

export async function analyzeDeckAi(
  deckId: number | string,
): Promise<DeckDoctorAIResponse> {
  const res = await apiClient.get<DeckDoctorAIResponse>(`/doctor/decks/${deckId}/ai`);
  return res.data;
}

export async function listDeckDiagnoses(
  deckId: number | string,
): Promise<DeckDiagnosisHistoryResponse[]> {
  const res = await apiClient.get<DeckDiagnosisHistoryResponse[]>(
    `/doctor/decks/${deckId}/diagnoses`,
  );
  return res.data;
}

export async function getDiagnosisById(
  diagnosisId: number | string,
): Promise<DeckDiagnosisDetailResponse> {
  const res = await apiClient.get<DeckDiagnosisDetailResponse>(
    `/doctor/diagnoses/${diagnosisId}`,
  );
  return res.data;
}
