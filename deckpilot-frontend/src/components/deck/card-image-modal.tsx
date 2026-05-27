"use client";

import { useEffect } from "react";
import Image from "next/image";
import type { CardResponse } from "@/features/cards/cards-types";

interface CardImageModalProps {
  card: CardResponse | null;
  copies?: number;
  onClose: () => void;
}

export function CardImageModal({
  card,
  copies,
  onClose,
}: Readonly<CardImageModalProps>) {
  useEffect(() => {
    if (!card) return;
    function handleKey(e: KeyboardEvent) {
      if (e.key === "Escape") onClose();
    }
    window.addEventListener("keydown", handleKey);
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      window.removeEventListener("keydown", handleKey);
      document.body.style.overflow = prev;
    };
  }, [card, onClose]);

  if (!card) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label={card.name}
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-sm"
      onClick={onClose}
    >
      <div
        className="relative grid max-h-[90vh] w-full max-w-4xl gap-6 overflow-y-auto rounded-2xl border border-zinc-800 bg-zinc-950 p-6 md:grid-cols-[minmax(0,1fr)_minmax(0,1.2fr)]"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          onClick={onClose}
          className="absolute right-3 top-3 z-10 inline-flex h-8 w-8 items-center justify-center rounded-full border border-zinc-700 bg-zinc-900/80 text-zinc-300 transition hover:border-zinc-500 hover:text-white"
          aria-label="Fechar"
        >
          ✕
        </button>

        <div className="flex items-start justify-center">
          {card.imageUrl ? (
            <Image
              src={card.imageUrl}
              alt={card.name}
              width={421}
              height={614}
              className="h-auto w-full max-w-sm rounded-lg border border-zinc-800 object-contain"
              unoptimized
            />
          ) : (
            <div className="flex aspect-[421/614] w-full max-w-sm items-center justify-center rounded-lg border border-zinc-800 bg-zinc-900 text-xs text-zinc-500">
              Sem imagem
            </div>
          )}
        </div>

        <div className="flex min-w-0 flex-col gap-3">
          <header>
            <h2 className="text-xl font-bold text-zinc-50">{card.name}</h2>
            <p className="mt-1 text-xs text-zinc-500">
              {card.cardType || "—"}
              {card.race ? ` · ${card.race}` : ""}
              {card.attribute ? ` · ${card.attribute}` : ""}
            </p>
          </header>

          {typeof copies === "number" && (
            <p className="text-xs text-indigo-300">
              Quantidade no deck: <span className="font-semibold">x{copies}</span>
            </p>
          )}

          <dl className="grid grid-cols-3 gap-2 text-center text-xs">
            <div className="rounded-md border border-zinc-800 bg-zinc-900/50 px-2 py-2">
              <dt className="text-zinc-500">Level</dt>
              <dd className="mt-0.5 text-sm font-semibold text-zinc-100">
                {card.level ?? "—"}
              </dd>
            </div>
            <div className="rounded-md border border-zinc-800 bg-zinc-900/50 px-2 py-2">
              <dt className="text-zinc-500">ATK</dt>
              <dd className="mt-0.5 text-sm font-semibold text-zinc-100">
                {card.atk ?? "—"}
              </dd>
            </div>
            <div className="rounded-md border border-zinc-800 bg-zinc-900/50 px-2 py-2">
              <dt className="text-zinc-500">DEF</dt>
              <dd className="mt-0.5 text-sm font-semibold text-zinc-100">
                {card.defense ?? "—"}
              </dd>
            </div>
          </dl>

          {card.description && (
            <div className="rounded-md border border-zinc-800 bg-zinc-900/50 p-3">
              <p className="text-[11px] uppercase tracking-wider text-zinc-500">
                Descrição
              </p>
              <p className="mt-1 whitespace-pre-wrap text-sm leading-relaxed text-zinc-200">
                {card.description}
              </p>
            </div>
          )}

          <p className="text-[11px] text-zinc-600">Origem: {card.source}</p>
        </div>
      </div>
    </div>
  );
}
