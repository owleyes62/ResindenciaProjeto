"use client";

import { use, useEffect, useState } from "react";
import Link from "next/link";
import { getDeckById } from "@/features/decks/decks-api";
import type { DeckResponse } from "@/features/decks/decks-types";
import {
  analyzeDeckAi,
  analyzeDeckLocal,
  listDeckDiagnoses,
} from "@/features/doctor/doctor-api";
import type {
  DeckDiagnosisHistoryResponse,
  DeckDoctorAIResponse,
  DeckDoctorResponse,
} from "@/features/doctor/doctor-types";
import { extractApiError } from "@/features/api/api-client";
import { LoadingState } from "@/components/ui/loading-state";
import { ErrorState } from "@/components/ui/error-state";
import { EmptyState } from "@/components/ui/empty-state";
import { DoctorResult } from "@/components/doctor/doctor-result";

interface EvaluatePageProps {
  params: Promise<{ deckId: string }>;
}

type DoctorAny = DeckDoctorResponse | DeckDoctorAIResponse;

export default function EvaluatePage(props: Readonly<EvaluatePageProps>) {
  const { deckId } = use(props.params);

  const [deck, setDeck] = useState<DeckResponse | null>(null);
  const [deckError, setDeckError] = useState<string | null>(null);
  const [isLoadingDeck, setIsLoadingDeck] = useState(true);
  const [deckReloadKey, setDeckReloadKey] = useState(0);

  const [result, setResult] = useState<DoctorAny | null>(null);
  const [resultError, setResultError] = useState<string | null>(null);
  const [analyzing, setAnalyzing] = useState<"local" | "ai" | null>(null);

  const [history, setHistory] = useState<DeckDiagnosisHistoryResponse[]>([]);
  const [historyError, setHistoryError] = useState<string | null>(null);
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
  const [historyReloadKey, setHistoryReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;
    async function fetchDeck() {
      try {
        const d = await getDeckById(deckId);
        if (cancelled) return;
        setDeck(d);
        setDeckError(null);
      } catch (err) {
        if (cancelled) return;
        setDeckError(extractApiError(err, "Não foi possível carregar o deck."));
      } finally {
        if (!cancelled) setIsLoadingDeck(false);
      }
    }
    void fetchDeck();
    return () => {
      cancelled = true;
    };
  }, [deckId, deckReloadKey]);

  useEffect(() => {
    let cancelled = false;
    async function fetchHistory() {
      try {
        const h = await listDeckDiagnoses(deckId);
        if (cancelled) return;
        setHistory(h ?? []);
        setHistoryError(null);
      } catch (err) {
        if (cancelled) return;
        setHistoryError(
          extractApiError(err, "Não foi possível carregar o histórico."),
        );
      } finally {
        if (!cancelled) setIsLoadingHistory(false);
      }
    }
    void fetchHistory();
    return () => {
      cancelled = true;
    };
  }, [deckId, historyReloadKey]);

  function retryDeck() {
    setIsLoadingDeck(true);
    setDeckError(null);
    setDeckReloadKey((k) => k + 1);
  }

  function retryHistory() {
    setIsLoadingHistory(true);
    setHistoryError(null);
    setHistoryReloadKey((k) => k + 1);
  }

  function refreshHistory() {
    setIsLoadingHistory(true);
    setHistoryReloadKey((k) => k + 1);
  }

  async function runLocal() {
    setAnalyzing("local");
    setResultError(null);
    try {
      const r = await analyzeDeckLocal(deckId);
      setResult(r);
      refreshHistory();
    } catch (err) {
      setResultError(extractApiError(err, "Falha na análise local."));
    } finally {
      setAnalyzing(null);
    }
  }

  async function runAi() {
    setAnalyzing("ai");
    setResultError(null);
    try {
      const r = await analyzeDeckAi(deckId);
      setResult(r);
      refreshHistory();
    } catch (err) {
      setResultError(extractApiError(err, "Falha na análise com IA."));
    } finally {
      setAnalyzing(null);
    }
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between gap-4">
        <Link
          href={`/decks/${deckId}`}
          className="text-sm text-zinc-400 transition hover:text-zinc-100"
        >
          ← Voltar para o deck
        </Link>
      </div>

      <header className="mb-6 rounded-2xl border border-zinc-800 bg-zinc-900/40 p-5">
        <h1 className="text-xl font-bold tracking-tight text-zinc-50">
          Deck Doctor
        </h1>
        {isLoadingDeck && <LoadingState message="Carregando deck..." />}
        {deckError && !isLoadingDeck && (
          <ErrorState message={deckError} onRetry={retryDeck} />
        )}
        {!isLoadingDeck && !deckError && deck && (
          <p className="mt-1 text-sm text-zinc-400">
            Avaliando <span className="font-semibold text-zinc-200">{deck.name}</span>{" "}
            · {deck.archetype} · {deck.playStyle}
          </p>
        )}

        <div className="mt-4 flex flex-wrap gap-2">
          <button
            type="button"
            onClick={runLocal}
            disabled={!!analyzing}
            className="inline-flex items-center justify-center rounded-md border border-zinc-700 bg-zinc-900 px-4 py-2 text-sm font-semibold text-zinc-100 transition hover:border-zinc-500 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {analyzing === "local" ? "Analisando..." : "Análise local"}
          </button>
          <button
            type="button"
            onClick={runAi}
            disabled={!!analyzing}
            className="inline-flex items-center justify-center rounded-md bg-indigo-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-400 disabled:cursor-not-allowed disabled:bg-indigo-500/50"
          >
            {analyzing === "ai" ? "Analisando..." : "Análise com IA"}
          </button>
        </div>
      </header>

      <section className="mb-8">
        {resultError && (
          <div className="mb-4">
            <ErrorState message={resultError} />
          </div>
        )}

        {!result && !resultError && !analyzing && (
          <EmptyState
            title="Nenhum diagnóstico ainda"
            description="Escolha análise local ou análise com IA para começar."
          />
        )}

        {analyzing && <LoadingState message="Avaliando o deck..." />}

        {result && !analyzing && <DoctorResult result={result} />}
      </section>

      <section>
        <h2 className="mb-3 text-sm font-semibold tracking-wide text-zinc-100">
          Histórico de diagnósticos
        </h2>
        {isLoadingHistory && <LoadingState message="Carregando histórico..." />}
        {historyError && !isLoadingHistory && (
          <ErrorState message={historyError} onRetry={retryHistory} />
        )}
        {!isLoadingHistory && !historyError && history.length === 0 && (
          <EmptyState
            title="Sem diagnósticos anteriores"
            description="Quando você rodar uma análise, ela aparecerá aqui."
          />
        )}
        {!isLoadingHistory && !historyError && history.length > 0 && (
          <ul className="grid gap-2">
            {history.map((h) => {
              const date = new Date(h.createdAt);
              const dateLabel = Number.isNaN(date.getTime())
                ? h.createdAt
                : date.toLocaleString();
              return (
                <li
                  key={h.id}
                  className="rounded-md border border-zinc-800 bg-zinc-900/40 px-3 py-3"
                >
                  <div className="flex flex-wrap items-center justify-between gap-2 text-xs text-zinc-500">
                    <span>
                      #{h.id} · {h.source}
                    </span>
                    <span>{dateLabel}</span>
                  </div>
                  <p className="mt-1 text-sm text-zinc-200">{h.summary}</p>
                </li>
              );
            })}
          </ul>
        )}
      </section>
    </div>
  );
}
