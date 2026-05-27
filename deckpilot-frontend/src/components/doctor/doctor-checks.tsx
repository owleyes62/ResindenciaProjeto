import type { DeckDoctorCheckResponse } from "@/features/doctor/doctor-types";

interface DoctorChecksProps {
  checks: DeckDoctorCheckResponse[];
}

export function DoctorChecks({ checks }: DoctorChecksProps) {
  if (!checks || checks.length === 0) return null;
  return (
    <section className="rounded-xl border border-zinc-800 bg-zinc-900/40 p-4">
      <h3 className="text-sm font-semibold text-zinc-100">Checagens</h3>
      <ul className="mt-3 space-y-2">
        {checks.map((c, i) => (
          <li
            key={`${c.name}-${i}`}
            className="flex items-start gap-3 rounded-md border border-zinc-800 bg-zinc-950/40 p-3"
          >
            <span
              aria-hidden
              className={`mt-0.5 inline-flex h-5 w-5 items-center justify-center rounded-full text-xs font-bold ${
                c.passed
                  ? "bg-emerald-500/20 text-emerald-300"
                  : "bg-red-500/20 text-red-300"
              }`}
            >
              {c.passed ? "✓" : "✕"}
            </span>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-medium text-zinc-100">{c.name}</p>
              <p className="mt-0.5 text-xs text-zinc-400">{c.message}</p>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}
