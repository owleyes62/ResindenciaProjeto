"use client";

import Image from "next/image";
import type { CardResponse } from "@/features/cards/cards-types";
import type { DeckCardResponse } from "@/features/decks/decks-types";

interface DeckCardItemProps {
  card: DeckCardResponse;
  details?: CardResponse | null;
  onClick?: () => void;
}

export function DeckCardItem({
  card,
  details,
  onClick,
}: Readonly<DeckCardItemProps>) {
  const thumb = details?.imageSmallUrl ?? details?.imageUrl ?? null;
  const interactive = !!onClick;

  return (
    <li>
      <button
        type="button"
        onClick={onClick}
        disabled={!interactive}
        className={`flex w-full items-center gap-3 rounded-md border border-zinc-800 bg-zinc-900/40 px-3 py-2 text-left transition ${
          interactive
            ? "cursor-pointer hover:border-indigo-500/60 hover:bg-zinc-900/70 focus:outline-none focus:ring-2 focus:ring-indigo-500/40"
            : "cursor-default"
        }`}
      >
        <span className="inline-flex h-7 min-w-7 shrink-0 items-center justify-center rounded-md border border-indigo-500/40 bg-indigo-500/10 px-2 text-xs font-semibold text-indigo-200">
          x{card.copies}
        </span>

        <span className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-md border border-zinc-800 bg-zinc-950">
          {thumb ? (
            <Image
              src={thumb}
              alt={card.cardName}
              width={40}
              height={40}
              className="h-10 w-10 object-cover"
              unoptimized
            />
          ) : (
            <span className="text-[10px] text-zinc-600">—</span>
          )}
        </span>

        <span
          className="min-w-0 flex-1 truncate text-sm text-zinc-100"
          title={card.cardName}
        >
          {card.cardName}
        </span>

        <span className="shrink-0 text-[10px] uppercase tracking-wider text-zinc-500">
          {card.section}
        </span>
      </button>
    </li>
  );
}
