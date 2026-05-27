import Link from "next/link";

const PILLARS = [
  {
    title: "Chat de geração",
    description:
      "Converse com a IA descrevendo o estilo de deck que deseja e receba uma decklist pronta em segundos.",
  },
  {
    title: "Visualização do deck",
    description:
      "Veja Main, Extra e Side Decks organizados, com contagens e metadados como arquétipo, formato e estilo.",
  },
  {
    title: "Deck Doctor",
    description:
      "Avalie pontos fortes, riscos e sugestões do seu deck, com análise local ou comentário gerado por IA.",
  },
];

export default function HomePage() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-12">
      <section className="rounded-2xl border border-zinc-800 bg-gradient-to-b from-zinc-900/80 to-zinc-950 px-6 py-12 text-center sm:px-12">
        <p className="text-xs font-medium uppercase tracking-[0.2em] text-indigo-400">
          Copiloto de IA · Yu-Gi-Oh!
        </p>
        <h1 className="mt-3 text-4xl font-bold tracking-tight text-zinc-50 sm:text-5xl">
          Monte e avalie decks com o DeckPilot
        </h1>
        <p className="mx-auto mt-4 max-w-2xl text-base text-zinc-400 sm:text-lg">
          Descreva o que você quer jogar e deixe a IA sugerir uma decklist. Salve seus
          decks, visualize por seções e use o Deck Doctor para entender riscos e
          melhorias.
        </p>
        <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
          <Link
            href="/generate"
            className="inline-flex w-full items-center justify-center rounded-md bg-indigo-500 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-400 sm:w-auto"
          >
            Gerar deck com IA
          </Link>
          <Link
            href="/decks"
            className="inline-flex w-full items-center justify-center rounded-md border border-zinc-700 bg-zinc-900 px-5 py-2.5 text-sm font-semibold text-zinc-100 transition hover:border-zinc-500 hover:bg-zinc-800 sm:w-auto"
          >
            Ver decks salvos
          </Link>
        </div>
      </section>

      <section className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {PILLARS.map((p) => (
          <article
            key={p.title}
            className="rounded-xl border border-zinc-800 bg-zinc-900/40 p-5 transition hover:border-indigo-500/40"
          >
            <h2 className="text-base font-semibold text-zinc-100">{p.title}</h2>
            <p className="mt-2 text-sm text-zinc-400">{p.description}</p>
          </article>
        ))}
      </section>
    </div>
  );
}
