"use client";

import { use, useEffect, useState } from "react";
import Link from "next/link";
import { getDeckById } from "@/features/decks/decks-api";
import type { DeckResponse } from "@/features/decks/decks-types";
import { extractApiError } from "@/features/api/api-client";
import { DeckViewer } from "@/components/deck/deck-viewer";
import { LoadingState } from "@/components/ui/loading-state";
import { ErrorState } from "@/components/ui/error-state";

interface DeckDetailPageProps {
  params: Promise<{ deckId: string }>;
}

export default function DeckDetailPage({ params }: DeckDetailPageProps) {
  const { deckId } = use(params);
  const [deck, setDeck] = useState<DeckResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;
    async function fetchData() {
      try {
        const result = await getDeckById(deckId);
        if (cancelled) return;
        setDeck(result);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(extractApiError(err, "Não foi possível carregar o deck."));
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }
    void fetchData();
    return () => {
      cancelled = true;
    };
  }, [deckId, reloadKey]);

  function handleRetry() {
    setIsLoading(true);
    setError(null);
    setReloadKey((k) => k + 1);
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between gap-4">
        <Link
          href="/decks"
          className="text-sm text-zinc-400 transition hover:text-zinc-100"
        >
          ← Voltar para decks
        </Link>
        {deck && (
          <Link
            href={`/evaluate/${deck.id}`}
            className="inline-flex items-center justify-center rounded-md bg-indigo-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-400"
          >
            Avaliar com Deck Doctor
          </Link>
        )}
      </div>

      {isLoading && <LoadingState message="Carregando deck..." />}

      {error && !isLoading && <ErrorState message={error} onRetry={handleRetry} />}

      {!isLoading && !error && (
        <DeckViewer
          deck={deck}
          emptyTitle="Deck não encontrado"
          emptyDescription="Esse deck pode ter sido removido ou não existe."
        />
      )}
    </div>
  );
}
