"use client";

import { useState } from "react";
import { ChatContainer } from "@/components/chat/chat-container";
import { DeckViewer } from "@/components/deck/deck-viewer";
import type { DeckResponse } from "@/features/decks/decks-types";

export default function GeneratePage() {
  const [deck, setDeck] = useState<DeckResponse | null>(null);

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <header className="mb-6">
        <h1 className="text-2xl font-bold tracking-tight text-zinc-50">
          Gerar deck com IA
        </h1>
        <p className="mt-1 text-sm text-zinc-400">
          Converse com o DeckPilot e veja o deck sugerido aparecer ao lado.
        </p>
      </header>

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
        <div className="lg:sticky lg:top-20 lg:max-h-[calc(100vh-7rem)]">
          <ChatContainer onDeckGenerated={setDeck} />
        </div>

        <div className="rounded-2xl border border-zinc-800 bg-zinc-950/60 p-4">
          <DeckViewer
            deck={deck}
            emptyTitle="Nenhum deck gerado ainda"
            emptyDescription="Envie uma mensagem descrevendo o deck que você quer montar. Quando a IA responder com uma decklist, ela aparecerá aqui."
          />
        </div>
      </div>
    </div>
  );
}
