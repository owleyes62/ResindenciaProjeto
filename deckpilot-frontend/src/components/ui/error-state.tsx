interface ErrorStateProps {
  title?: string;
  message: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = "Algo deu errado",
  message,
  onRetry,
}: ErrorStateProps) {
  return (
    <div className="rounded-lg border border-red-900/60 bg-red-950/40 px-4 py-5 text-red-100">
      <p className="text-sm font-semibold">{title}</p>
      <p className="mt-1 text-sm text-red-200/80">{message}</p>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="mt-3 inline-flex items-center rounded-md border border-red-700/70 bg-red-900/40 px-3 py-1.5 text-xs font-medium text-red-50 hover:bg-red-900/70"
        >
          Tentar novamente
        </button>
      )}
    </div>
  );
}
