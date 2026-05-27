"use client";

import { useEffect, useRef } from "react";
import Link from "next/link";
import type { ChatMessageResponse, ChatRole } from "@/features/chat/chat-types";

interface LocalChatMessage {
  id: number | string;
  role: ChatRole | "DECK_CTA";
  content: string;
  deckId?: number;
  deckName?: string;
}

interface ChatMessageListProps {
  messages: LocalChatMessage[];
  isPending: boolean;
}

function roleLabel(role: ChatRole | "DECK_CTA"): string {
  if (role === "USER") return "Você";
  if (role === "ASSISTANT") return "DeckPilot";
  if (role === "SYSTEM") return "Sistema";
  return role;
}

function roleStyles(role: ChatRole | "DECK_CTA"): string {
  if (role === "USER") {
    return "self-end bg-indigo-500/15 border-indigo-500/40 text-indigo-50";
  }
  if (role === "SYSTEM") {
    return "self-start bg-zinc-900/60 border-zinc-800 text-zinc-400 italic";
  }
  return "self-start bg-zinc-900/40 border-zinc-800 text-zinc-100";
}

export function ChatMessageList({
  messages,
  isPending,
}: Readonly<ChatMessageListProps>) {
  const bottomRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [messages.length, isPending]);

  return (
    <div className="flex flex-1 flex-col gap-3 overflow-y-auto px-1 pb-2">
      {messages.map((m) => {
        if (m.role === "DECK_CTA" && typeof m.deckId === "number") {
          return (
            <div
              key={m.id}
              className="self-start max-w-[90%] rounded-lg border border-indigo-500/40 bg-indigo-500/10 px-3 py-3 text-sm text-indigo-50"
            >
              <p className="text-[10px] font-semibold uppercase tracking-wider text-indigo-300">
                Deck pronto
              </p>
              <p className="mt-1 leading-relaxed">
                {m.content}
                {m.deckName ? (
                  <>
                    {" "}
                    <span className="font-semibold">{m.deckName}</span>
                  </>
                ) : null}
              </p>
              <div className="mt-3">
                <Link
                  href={`/evaluate/${m.deckId}`}
                  className="inline-flex items-center justify-center rounded-md bg-indigo-500 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-indigo-400"
                >
                  Avaliar com Deck Doctor
                </Link>
              </div>
            </div>
          );
        }

        return (
          <div
            key={m.id}
            className={`max-w-[85%] rounded-lg border px-3 py-2 text-sm shadow-sm ${roleStyles(m.role)}`}
          >
            <p className="mb-1 text-[10px] font-semibold uppercase tracking-wider opacity-60">
              {roleLabel(m.role)}
            </p>
            <p className="whitespace-pre-wrap leading-relaxed">{m.content}</p>
          </div>
        );
      })}
      {isPending && (
        <div className="self-start rounded-lg border border-zinc-800 bg-zinc-900/40 px-3 py-2 text-sm text-zinc-400">
          <span
            aria-hidden
            className="mr-2 inline-block h-3 w-3 animate-spin rounded-full border-2 border-zinc-700 border-t-indigo-400"
          />
          DeckPilot está pensando...
        </div>
      )}
      <div ref={bottomRef} />
    </div>
  );
}

export type { LocalChatMessage };
export type { ChatMessageResponse };
