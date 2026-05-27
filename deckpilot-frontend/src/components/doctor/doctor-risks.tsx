interface DoctorRisksProps {
  items: string[];
}

export function DoctorRisks({ items }: DoctorRisksProps) {
  if (!items || items.length === 0) return null;
  return (
    <section className="rounded-xl border border-amber-900/60 bg-amber-950/30 p-4">
      <h3 className="text-sm font-semibold text-amber-200">Riscos</h3>
      <ul className="mt-2 space-y-1 text-sm text-amber-50">
        {items.map((s, i) => (
          <li key={i} className="flex gap-2">
            <span aria-hidden className="mt-1 inline-block h-1.5 w-1.5 rounded-full bg-amber-400" />
            <span>{s}</span>
          </li>
        ))}
      </ul>
    </section>
  );
}
