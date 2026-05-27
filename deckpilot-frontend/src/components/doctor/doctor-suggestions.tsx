interface DoctorSuggestionsProps {
  items: string[];
}

export function DoctorSuggestions({ items }: DoctorSuggestionsProps) {
  if (!items || items.length === 0) return null;
  return (
    <section className="rounded-xl border border-indigo-900/60 bg-indigo-950/30 p-4">
      <h3 className="text-sm font-semibold text-indigo-200">Sugestões</h3>
      <ul className="mt-2 space-y-1 text-sm text-indigo-50">
        {items.map((s, i) => (
          <li key={i} className="flex gap-2">
            <span aria-hidden className="mt-1 inline-block h-1.5 w-1.5 rounded-full bg-indigo-400" />
            <span>{s}</span>
          </li>
        ))}
      </ul>
    </section>
  );
}
