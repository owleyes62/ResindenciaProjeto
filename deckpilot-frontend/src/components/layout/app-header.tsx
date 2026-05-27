import Link from "next/link";
import { API_BASE_URL } from "@/features/api/api-client";

const NAV_LINKS = [
  { href: "/", label: "Início" },
  { href: "/generate", label: "Gerar Deck" },
  { href: "/decks", label: "Decks" },
];

export function AppHeader() {
  const swaggerUrl = `${API_BASE_URL.replace(/\/$/, "")}/swagger-ui.html`;

  return (
    <header className="sticky top-0 z-30 border-b border-zinc-900 bg-black/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <Link
          href="/"
          className="flex items-center gap-2 text-zinc-100 transition hover:text-white"
        >
          <span className="inline-block h-2 w-2 rounded-full bg-indigo-400" />
          <span className="text-base font-semibold tracking-tight">DeckPilot</span>
        </Link>

        <nav className="flex items-center gap-1 text-sm">
          {NAV_LINKS.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className="rounded-md px-3 py-1.5 text-zinc-400 transition hover:bg-zinc-900 hover:text-zinc-100"
            >
              {link.label}
            </Link>
          ))}
          <a
            href={swaggerUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="ml-1 rounded-md border border-zinc-800 px-3 py-1.5 text-zinc-300 transition hover:border-indigo-500/60 hover:text-white"
          >
            Swagger/API
          </a>
        </nav>
      </div>
    </header>
  );
}
