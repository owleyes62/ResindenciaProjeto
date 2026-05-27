"use client";

import { useEffect, useMemo, useState } from "react";
import type { DeckCardResponse, DeckResponse } from "@/features/decks/decks-types";
import type { CardResponse } from "@/features/cards/cards-types";
import { getCardsByIds } from "@/features/cards/cards-api";
import { EmptyState } from "@/components/ui/empty-state";
import { DeckSection } from "./deck-section";
import { CardImageModal } from "./card-image-modal";

interface DeckViewerProps {
  deck: DeckResponse | null;
  emptyTitle?: string;
  emptyDescription?: string;
}

interface SelectedCard {
  card: CardResponse;
  copies: number;
}

function countCopies(items: DeckCardResponse[]): number {
  return items.reduce((sum, c) => sum + (c.copies ?? 0), 0);
}

function collectIds(deck: DeckResponse): number[] {
  const ids = [
    ...deck.mainDeck.map((c) => c.cardId),
    ...deck.extraDeck.map((c) => c.cardId),
    ...deck.sideDeck.map((c) => c.cardId),
  ];
  return Array.from(new Set(ids.filter((id) => Number.isFinite(id))));
}

export function DeckViewer({
  deck,
  emptyTitle = "Nenhum deck para mostrar",
  emptyDescription = "Quando a IA gerar um deck, ele aparecerá aqui.",
}: Readonly<DeckViewerProps>) {
  const [detailsById, setDetailsById] = useState<Map<number, CardResponse>>(
    new Map(),
  );
  const [selected, setSelected] = useState<SelectedCard | null>(null);

  const idsKey = useMemo(() => (deck ? collectIds(deck).join(",") : ""), [deck]);

  useEffect(() => {
    let cancelled = false;
    async function fetchDetails() {
      const ids =
        idsKey.length === 0
          ? []
          : idsKey.split(",").map(Number).filter((n) => Number.isFinite(n));
      try {
        const cards = ids.length === 0 ? [] : await getCardsByIds(ids);
        if (cancelled) return;
        const map = new Map<number, CardResponse>();
        for (const c of cards) map.set(c.id, c);
        setDetailsById(map);
      } catch {
        if (!cancelled) setDetailsById(new Map());
      }
    }
    void fetchDetails();
    return () => {
      cancelled = true;
    };
  }, [idsKey]);

  if (!deck) {
    return <EmptyState title={emptyTitle} description={emptyDescription} />;
  }

  const mainCount = countCopies(deck.mainDeck);
  const extraCount = countCopies(deck.extraDeck);
  const sideCount = countCopies(deck.sideDeck);

  function handleCardClick(deckCard: DeckCardResponse, details: CardResponse | null) {
    if (!details) return;
    setSelected({ card: details, copies: deckCard.copies });
  }

  return (
    <div className="flex flex-col gap-4">
      <header className="rounded-xl border border-zinc-800 bg-zinc-900/40 p-4">
        <h2 className="text-lg font-semibold text-zinc-50">{deck.name}</h2>
        <p className="mt-1 text-xs text-zinc-400">
          {deck.archetype} · {deck.playStyle} · Formato {deck.format || "—"} · Origem{" "}
          {deck.source}
        </p>

        {(deck.winCondition || deck.howToPilot) && (
          <div className="mt-4 grid gap-3 sm:grid-cols-2">
            {deck.winCondition && (
              <div className="rounded-md border border-zinc-800 bg-zinc-950/60 p-3">
                <p className="text-[11px] uppercase tracking-wider text-zinc-500">
                  Win condition
                </p>
                <p className="mt-1 text-sm text-zinc-200">{deck.winCondition}</p>
              </div>
            )}
            {deck.howToPilot && (
              <div className="rounded-md border border-zinc-800 bg-zinc-950/60 p-3">
                <p className="text-[11px] uppercase tracking-wider text-zinc-500">
                  Como pilotar
                </p>
                <p className="mt-1 text-sm text-zinc-200">{deck.howToPilot}</p>
              </div>
            )}
          </div>
        )}

        <dl className="mt-4 grid grid-cols-3 gap-2 text-center text-xs">
          <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
            <dt className="text-zinc-500">Main</dt>
            <dd className="mt-0.5 text-base font-semibold text-zinc-100">
              {mainCount}
            </dd>
          </div>
          <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
            <dt className="text-zinc-500">Extra</dt>
            <dd className="mt-0.5 text-base font-semibold text-zinc-100">
              {extraCount}
            </dd>
          </div>
          <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
            <dt className="text-zinc-500">Side</dt>
            <dd className="mt-0.5 text-base font-semibold text-zinc-100">
              {sideCount}
            </dd>
          </div>
        </dl>
      </header>

      <DeckSection
        title="Main Deck"
        cards={deck.mainDeck}
        detailsById={detailsById}
        onCardClick={handleCardClick}
      />
      <DeckSection
        title="Extra Deck"
        cards={deck.extraDeck}
        detailsById={detailsById}
        onCardClick={handleCardClick}
      />
      <DeckSection
        title="Side Deck"
        cards={deck.sideDeck}
        detailsById={detailsById}
        onCardClick={handleCardClick}
      />

      <CardImageModal
        card={selected?.card ?? null}
        copies={selected?.copies}
        onClose={() => setSelected(null)}
      />
    </div>
  );
}
