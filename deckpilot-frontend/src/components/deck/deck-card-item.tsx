import type { DeckCardResponse } from "@/features/decks/decks-types";

interface DeckCardItemProps {
  card: DeckCardResponse;
}

export function DeckCardItem({ card }: DeckCardItemProps) {
  return (
    <li className="flex items-center justify-between gap-3 rounded-md border border-zinc-800 bg-zinc-900/40 px-3 py-2">
      <div className="flex items-center gap-3 min-w-0">
        <span className="inline-flex h-7 min-w-7 items-center justify-center rounded-md border border-indigo-500/40 bg-indigo-500/10 px-2 text-xs font-semibold text-indigo-200">
          x{card.copies}
        </span>
        <span className="truncate text-sm text-zinc-100" title={card.cardName}>
          {card.cardName}
        </span>
      </div>
      <span className="shrink-0 text-[10px] uppercase tracking-wider text-zinc-500">
        {card.section}
      </span>
    </li>
  );
}
