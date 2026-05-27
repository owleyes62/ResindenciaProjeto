export interface DeckDoctorCheckResponse {
  name: string;
  passed: boolean;
  message: string;
}

export interface DeckDoctorResponse {
  diagnosisId: number;
  deckId: number;
  deckName: string;
  summary: string;
  strengths: string[];
  risks: string[];
  suggestions: string[];
  checks: DeckDoctorCheckResponse[];
  source: string;
  createdAt: string;
}

export interface DeckDoctorAIResponse {
  diagnosisId: number;
  deckId: number;
  deckName: string;
  summary: string;
  strengths: string[];
  risks: string[];
  suggestions: string[];
  aiCommentary: string;
  source: string;
  createdAt: string;
}

export interface DeckDiagnosisHistoryResponse {
  id: number;
  deckId: number;
  summary: string;
  source: string;
  createdAt: string;
}

export interface DeckDiagnosisDetailResponse {
  id: number;
  deckId: number;
  deckName: string;
  summary: string;
  strengths: string;
  risks: string;
  suggestions: string;
  checksJson: string;
  source: string;
  createdAt: string;
}
