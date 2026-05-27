import axios, { AxiosError } from "axios";

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export type ApiErrorShape = {
  message?: string;
  error?: string;
  status?: number;
  path?: string;
};

export function extractApiError(err: unknown, fallback = "Erro inesperado"): string {
  if (axios.isAxiosError(err)) {
    const axiosErr = err as AxiosError<ApiErrorShape | string>;
    const data = axiosErr.response?.data;

    if (typeof data === "string" && data.trim().length > 0) {
      return data;
    }
    if (data && typeof data === "object") {
      if (data.message) return data.message;
      if (data.error) return data.error;
    }
    if (axiosErr.message) return axiosErr.message;
  }
  if (err instanceof Error && err.message) {
    return err.message;
  }
  return fallback;
}
