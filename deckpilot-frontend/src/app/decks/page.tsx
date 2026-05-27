"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { listDecks } from "@/features/decks/decks-api";
import type { DeckResponse } from "@/features/decks/decks-types";
import { extractApiError } from "@/features/api/api-client";
import { DeckSummaryCard } from "@/components/deck/deck-summary-card";
import { LoadingState } from "@/components/ui/loading-state";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";

export default function DecksPage() {
  const [decks, setDecks] = useState<DeckResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;
    async function fetchData() {
      try {
        const page = await listDecks(0, 20);
        if (cancelled) return;
        setDecks(page.content ?? []);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(extractApiError(err, "Não foi possível carregar os decks."));
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }
    void fetchData();
    return () => {
      cancelled = true;
    };
  }, [reloadKey]);

  function handleRetry() {
    setIsLoading(true);
    setError(null);
    setReloadKey((k) => k + 1);
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <header className="mb-6 flex items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-zinc-50">
            Decks salvos
          </h1>
          <p className="mt-1 text-sm text-zinc-400">
            Decks que você (ou a IA) salvou no banco.
          </p>
        </div>
        <Link
          href="/generate"
          className="inline-flex items-center justify-center rounded-md bg-indigo-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-400"
        >
          Novo deck
        </Link>
      </header>

      {isLoading && <LoadingState message="Carregando decks..." />}

      {error && !isLoading && <ErrorState message={error} onRetry={handleRetry} />}

      {!isLoading && !error && decks.length === 0 && (
        <EmptyState
          title="Nenhum deck salvo ainda"
          description="Use o chat de geração para criar seu primeiro deck."
          action={
            <Link
              href="/generate"
              className="inline-flex items-center justify-center rounded-md bg-indigo-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-400"
            >
              Gerar deck
            </Link>
          }
        />
      )}

      {!isLoading && !error && decks.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {decks.map((deck) => (
            <DeckSummaryCard key={deck.id} deck={deck} />
          ))}
        </div>
      )}
    </div>
  );
}
