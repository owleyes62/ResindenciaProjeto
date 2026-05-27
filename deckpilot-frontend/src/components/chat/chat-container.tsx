"use client";

import { useEffect, useState } from "react";
import { createChatSession, sendChatMessage } from "@/features/chat/chat-api";
import type { ChatSessionResponse } from "@/features/chat/chat-types";
import type { DeckResponse } from "@/features/decks/decks-types";
import { extractApiError } from "@/features/api/api-client";
import { ChatMessageList, type LocalChatMessage } from "./chat-message-list";
import { ChatInput } from "./chat-input";
import { ErrorState } from "@/components/ui/error-state";
import { LoadingState } from "@/components/ui/loading-state";

interface ChatContainerProps {
  onDeckGenerated: (deck: DeckResponse) => void;
}

const INITIAL_ASSISTANT_MESSAGE: LocalChatMessage = {
  id: "initial-assistant",
  role: "ASSISTANT",
  content: "Olá! Me diga que tipo de deck você quer montar.",
};

export function ChatContainer({ onDeckGenerated }: Readonly<ChatContainerProps>) {
  const [session, setSession] = useState<ChatSessionResponse | null>(null);
  const [messages, setMessages] = useState<LocalChatMessage[]>([
    INITIAL_ASSISTANT_MESSAGE,
  ]);
  const [isCreatingSession, setIsCreatingSession] = useState(true);
  const [isSending, setIsSending] = useState(false);
  const [sessionError, setSessionError] = useState<string | null>(null);
  const [sendError, setSendError] = useState<string | null>(null);
  const [sessionReloadKey, setSessionReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;
    async function init() {
      try {
        const newSession = await createChatSession();
        if (cancelled) return;
        setSession(newSession);
        setSessionError(null);
      } catch (err) {
        if (cancelled) return;
        setSessionError(extractApiError(err, "Não foi possível iniciar a sessão."));
      } finally {
        if (!cancelled) setIsCreatingSession(false);
      }
    }
    void init();
    return () => {
      cancelled = true;
    };
  }, [sessionReloadKey]);

  function retrySession() {
    setIsCreatingSession(true);
    setSessionError(null);
    setSession(null);
    setSessionReloadKey((k) => k + 1);
  }

  async function handleSubmit(content: string) {
    if (!session || isSending) return;
    setIsSending(true);
    setSendError(null);

    const optimisticUserMessage: LocalChatMessage = {
      id: `local-user-${Date.now()}`,
      role: "USER",
      content,
    };
    setMessages((prev) => [...prev, optimisticUserMessage]);

    try {
      const res = await sendChatMessage(session.id, content);
      const generated = res.generatedDeck?.deck ?? null;
      setMessages((prev) => {
        const withoutOptimistic = prev.filter((m) => m.id !== optimisticUserMessage.id);
        const next: LocalChatMessage[] = [
          ...withoutOptimistic,
          res.userMessage,
          res.assistantMessage,
        ];
        if (generated) {
          next.push({
            id: `deck-cta-${res.assistantMessage.id}-${generated.id}`,
            role: "DECK_CTA",
            content: "Quer ver os pontos fortes, riscos e sugestões deste deck?",
            deckId: generated.id,
            deckName: generated.name,
          });
        }
        return next;
      });
      if (generated) {
        onDeckGenerated(generated);
      }
    } catch (err) {
      setMessages((prev) => prev.filter((m) => m.id !== optimisticUserMessage.id));
      setSendError(extractApiError(err, "Não foi possível enviar a mensagem."));
    } finally {
      setIsSending(false);
    }
  }

  let sessionLabel: string;
  if (session) sessionLabel = `Sessão #${session.id}`;
  else if (isCreatingSession) sessionLabel = "Iniciando sessão...";
  else sessionLabel = "Sessão não iniciada";

  return (
    <div className="flex h-full min-h-[600px] flex-col rounded-2xl border border-zinc-800 bg-zinc-950/60">
      <div className="border-b border-zinc-900 px-4 py-3">
        <p className="text-sm font-semibold text-zinc-100">Chat de geração</p>
        <p className="text-xs text-zinc-500">{sessionLabel}</p>
      </div>

      <div className="flex flex-1 flex-col gap-3 overflow-hidden px-4 py-3">
        {isCreatingSession && <LoadingState message="Iniciando sessão de chat..." />}

        {sessionError && (
          <ErrorState
            title="Falha ao iniciar a sessão"
            message={sessionError}
            onRetry={retrySession}
          />
        )}

        {!isCreatingSession && !sessionError && (
          <ChatMessageList messages={messages} isPending={isSending} />
        )}

        {sendError && (
          <div className="rounded-md border border-red-900/60 bg-red-950/40 px-3 py-2 text-xs text-red-100">
            {sendError}
          </div>
        )}
      </div>

      <div className="border-t border-zinc-900 px-4 py-3">
        <ChatInput
          onSubmit={handleSubmit}
          disabled={isCreatingSession || !!sessionError || isSending}
        />
      </div>
    </div>
  );
}
