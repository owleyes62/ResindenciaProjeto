import Link from "next/link";
import type { DeckResponse } from "@/features/decks/decks-types";

interface DeckSummaryCardProps {
  deck: DeckResponse;
}

function countCopies(items: DeckResponse["mainDeck"]): number {
  return items.reduce((sum, c) => sum + (c.copies ?? 0), 0);
}

export function DeckSummaryCard({ deck }: DeckSummaryCardProps) {
  const mainCount = countCopies(deck.mainDeck);
  const extraCount = countCopies(deck.extraDeck);
  const sideCount = countCopies(deck.sideDeck);

  return (
    <article className="flex flex-col rounded-xl border border-zinc-800 bg-zinc-900/40 p-4 transition hover:border-indigo-500/40">
      <header>
        <h3 className="truncate text-base font-semibold text-zinc-100" title={deck.name}>
          {deck.name}
        </h3>
        <p className="mt-1 text-xs text-zinc-500">
          {deck.archetype} · {deck.playStyle}
        </p>
      </header>

      <dl className="mt-4 grid grid-cols-3 gap-2 text-center text-xs">
        <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
          <dt className="text-zinc-500">Main</dt>
          <dd className="mt-0.5 text-sm font-semibold text-zinc-100">{mainCount}</dd>
        </div>
        <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
          <dt className="text-zinc-500">Extra</dt>
          <dd className="mt-0.5 text-sm font-semibold text-zinc-100">{extraCount}</dd>
        </div>
        <div className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-2">
          <dt className="text-zinc-500">Side</dt>
          <dd className="mt-0.5 text-sm font-semibold text-zinc-100">{sideCount}</dd>
        </div>
      </dl>

      <div className="mt-4 flex flex-wrap gap-2 text-[11px] text-zinc-400">
        <span className="rounded-md border border-zinc-800 bg-zinc-950/40 px-2 py-1">
          Formato: {deck.format || "—"}
        </span>
        <span className="rounded-md border border-zinc-800 bg-zinc-950/40 px-2 py-1">
          Origem: {deck.source}
        </span>
      </div>

      <div className="mt-5 flex items-center gap-2">
        <Link
          href={`/decks/${deck.id}`}
          className="inline-flex flex-1 items-center justify-center rounded-md border border-zinc-700 bg-zinc-900 px-3 py-2 text-xs font-semibold text-zinc-100 transition hover:border-zinc-500"
        >
          Ver deck
        </Link>
        <Link
          href={`/evaluate/${deck.id}`}
          className="inline-flex flex-1 items-center justify-center rounded-md bg-indigo-500 px-3 py-2 text-xs font-semibold text-white transition hover:bg-indigo-400"
        >
          Avaliar
        </Link>
      </div>
    </article>
  );
}
