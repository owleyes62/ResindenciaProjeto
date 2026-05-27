import type {
  DeckDoctorAIResponse,
  DeckDoctorCheckResponse,
  DeckDoctorResponse,
} from "@/features/doctor/doctor-types";
import { DoctorSummary } from "./doctor-summary";
import { DoctorStrengths } from "./doctor-strengths";
import { DoctorRisks } from "./doctor-risks";
import { DoctorSuggestions } from "./doctor-suggestions";
import { DoctorChecks } from "./doctor-checks";

interface DoctorResultProps {
  result: DeckDoctorResponse | DeckDoctorAIResponse;
}

function isAi(result: DoctorResultProps["result"]): result is DeckDoctorAIResponse {
  return "aiCommentary" in result;
}

function getChecks(result: DoctorResultProps["result"]): DeckDoctorCheckResponse[] {
  if ("checks" in result && Array.isArray(result.checks)) {
    return result.checks;
  }
  return [];
}

export function DoctorResult({ result }: DoctorResultProps) {
  const checks = getChecks(result);
  const ai = isAi(result) ? result : null;
  const createdAt = new Date(result.createdAt);
  const createdAtLabel = Number.isNaN(createdAt.getTime())
    ? result.createdAt
    : createdAt.toLocaleString();

  return (
    <div className="flex flex-col gap-4">
      <header className="flex flex-wrap items-center gap-2 text-xs text-zinc-500">
        <span className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-1">
          Diagnóstico #{result.diagnosisId}
        </span>
        <span className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-1">
          Origem: {result.source}
        </span>
        <span className="rounded-md border border-zinc-800 bg-zinc-950/60 px-2 py-1">
          {createdAtLabel}
        </span>
      </header>

      <DoctorSummary summary={result.summary} aiCommentary={ai?.aiCommentary} />
      <DoctorStrengths items={result.strengths} />
      <DoctorRisks items={result.risks} />
      <DoctorSuggestions items={result.suggestions} />
      <DoctorChecks checks={checks} />
    </div>
  );
}
