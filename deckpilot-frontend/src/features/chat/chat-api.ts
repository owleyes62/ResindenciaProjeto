import { apiClient } from "@/features/api/api-client";
import type { Page } from "@/features/api/page-types";
import type {
  ChatGeneratedDeckResponse,
  ChatMessageResponse,
  ChatSendMessageResponse,
  ChatSessionResponse,
} from "./chat-types";

export async function createChatSession(
  title?: string | null,
): Promise<ChatSessionResponse> {
  const res = await apiClient.post<ChatSessionResponse>("/chat/sessions", {
    title: title ?? null,
  });
  return res.data;
}

export async function listChatSessions(
  page = 0,
  size = 20,
): Promise<Page<ChatSessionResponse>> {
  const res = await apiClient.get<Page<ChatSessionResponse>>("/chat/sessions", {
    params: { page, size },
  });
  return res.data;
}

export async function getChatSession(
  sessionId: number | string,
): Promise<ChatSessionResponse> {
  const res = await apiClient.get<ChatSessionResponse>(`/chat/sessions/${sessionId}`);
  return res.data;
}

export async function getChatMessages(
  sessionId: number | string,
): Promise<ChatMessageResponse[]> {
  const res = await apiClient.get<ChatMessageResponse[]>(
    `/chat/sessions/${sessionId}/messages`,
  );
  return res.data;
}

export async function sendChatMessage(
  sessionId: number | string,
  content: string,
): Promise<ChatSendMessageResponse> {
  const res = await apiClient.post<ChatSendMessageResponse>(
    `/chat/sessions/${sessionId}/messages`,
    { content },
  );
  return res.data;
}

export async function listGeneratedDecks(
  sessionId: number | string,
): Promise<ChatGeneratedDeckResponse[]> {
  const res = await apiClient.get<ChatGeneratedDeckResponse[]>(
    `/chat/sessions/${sessionId}/generated-decks`,
  );
  return res.data;
}

export async function getGeneratedDeckByIndex(
  sessionId: number | string,
  generationIndex: number,
): Promise<ChatGeneratedDeckResponse> {
  const res = await apiClient.get<ChatGeneratedDeckResponse>(
    `/chat/sessions/${sessionId}/generated-decks/${generationIndex}`,
  );
  return res.data;
}
