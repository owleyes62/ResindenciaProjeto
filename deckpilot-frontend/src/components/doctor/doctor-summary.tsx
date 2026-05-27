interface DoctorSummaryProps {
  summary: string;
  aiCommentary?: string;
}

export function DoctorSummary({ summary, aiCommentary }: DoctorSummaryProps) {
  return (
    <section className="rounded-xl border border-zinc-800 bg-zinc-900/40 p-4">
      <h3 className="text-sm font-semibold text-zinc-100">Resumo</h3>
      <p className="mt-2 whitespace-pre-wrap text-sm leading-relaxed text-zinc-300">
        {summary}
      </p>
      {aiCommentary && (
        <div className="mt-4 rounded-md border border-indigo-500/30 bg-indigo-500/10 p-3">
          <p className="text-[11px] uppercase tracking-wider text-indigo-300">
            Comentário da IA
          </p>
          <p className="mt-1 whitespace-pre-wrap text-sm leading-relaxed text-indigo-50">
            {aiCommentary}
          </p>
        </div>
      )}
    </section>
  );
}
