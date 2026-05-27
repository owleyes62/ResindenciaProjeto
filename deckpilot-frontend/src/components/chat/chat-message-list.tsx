"use client";

import { useEffect, useRef } from "react";
import type { ChatMessageResponse, ChatRole } from "@/features/chat/chat-types";

interface LocalChatMessage {
  id: number | string;
  role: ChatRole;
  content: string;
}

interface ChatMessageListProps {
  messages: LocalChatMessage[];
  isPending: boolean;
}

function roleLabel(role: ChatRole): string {
  if (role === "USER") return "Você";
  if (role === "ASSISTANT") return "DeckPilot";
  if (role === "SYSTEM") return "Sistema";
  return role;
}

function roleStyles(role: ChatRole): string {
  if (role === "USER") {
    return "self-end bg-indigo-500/15 border-indigo-500/40 text-indigo-50";
  }
  if (role === "SYSTEM") {
    return "self-start bg-zinc-900/60 border-zinc-800 text-zinc-400 italic";
  }
  return "self-start bg-zinc-900/40 border-zinc-800 text-zinc-100";
}

export function ChatMessageList({ messages, isPending }: ChatMessageListProps) {
  const bottomRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [messages.length, isPending]);

  return (
    <div className="flex flex-1 flex-col gap-3 overflow-y-auto px-1 pb-2">
      {messages.map((m) => (
        <div
          key={m.id}
          className={`max-w-[85%] rounded-lg border px-3 py-2 text-sm shadow-sm ${roleStyles(m.role)}`}
        >
          <p className="mb-1 text-[10px] font-semibold uppercase tracking-wider opacity-60">
            {roleLabel(m.role)}
          </p>
          <p className="whitespace-pre-wrap leading-relaxed">{m.content}</p>
        </div>
      ))}
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
