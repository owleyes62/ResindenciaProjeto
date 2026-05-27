import type { CardResponse } from "@/features/cards/cards-types";
import type { DeckCardResponse } from "@/features/decks/decks-types";
import { DeckCardItem } from "./deck-card-item";

interface DeckSectionProps {
  title: string;
  cards: DeckCardResponse[];
  detailsById?: Map<number, CardResponse>;
  onCardClick?: (card: DeckCardResponse, details: CardResponse | null) => void;
}

export function DeckSection({
  title,
  cards,
  detailsById,
  onCardClick,
}: Readonly<DeckSectionProps>) {
  const total = cards.reduce((sum, c) => sum + (c.copies ?? 0), 0);

  return (
    <section className="rounded-xl border border-zinc-800 bg-zinc-900/30 p-4">
      <header className="mb-3 flex items-center justify-between">
        <h3 className="text-sm font-semibold tracking-wide text-zinc-100">{title}</h3>
        <span className="rounded-full border border-zinc-700 bg-zinc-900 px-2 py-0.5 text-xs text-zinc-300">
          {total} cartas
        </span>
      </header>
      {cards.length === 0 ? (
        <p className="text-xs text-zinc-500">Nenhuma carta nesta seção.</p>
      ) : (
        <ul className="grid gap-2">
          {cards.map((c) => {
            const details = detailsById?.get(c.cardId) ?? null;
            return (
              <DeckCardItem
                key={`${c.cardId}-${c.cardName}`}
                card={c}
                details={details}
                onClick={
                  onCardClick ? () => onCardClick(c, details) : undefined
                }
              />
            );
          })}
        </ul>
      )}
    </section>
  );
}
