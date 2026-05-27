import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AppHeader } from "@/components/layout/app-header";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "DeckPilot — copiloto de IA para Yu-Gi-Oh!",
  description:
    "Monte e avalie decks de Yu-Gi-Oh! com a ajuda do DeckPilot, um copiloto de IA.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="pt-BR"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full bg-zinc-950 text-zinc-100">
        <div className="flex min-h-screen flex-col">
          <AppHeader />
          <main className="flex-1">{children}</main>
          <footer className="border-t border-zinc-900 py-4 text-center text-xs text-zinc-500">
            DeckPilot · Front Next.js · Backend Spring Boot
          </footer>
        </div>
      </body>
    </html>
  );
}
