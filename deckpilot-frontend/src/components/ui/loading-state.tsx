interface LoadingStateProps {
  message?: string;
}

export function LoadingState({ message = "Carregando..." }: LoadingStateProps) {
  return (
    <div className="flex items-center justify-center gap-3 rounded-lg border border-zinc-800 bg-zinc-900/40 px-4 py-6 text-zinc-300">
      <span
        aria-hidden
        className="h-4 w-4 animate-spin rounded-full border-2 border-zinc-600 border-t-indigo-400"
      />
      <span className="text-sm">{message}</span>
    </div>
  );
}
