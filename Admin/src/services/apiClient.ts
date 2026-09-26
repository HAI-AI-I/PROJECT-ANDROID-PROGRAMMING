const API_URL = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8386/api/v1").replace(/\/$/, "");
const API_ORIGIN = API_URL.replace(/\/api\/v1$/, "");

export const AUTH_UNAUTHORIZED_EVENT = "auth:unauthorized";

export function clearAuthSession() {
  if (typeof window === "undefined") return;
  window.localStorage.removeItem("library_access_token");
  window.localStorage.removeItem("library_user");
}

const getAccessToken = () =>
  typeof window === "undefined" ? null : window.localStorage.getItem("library_access_token");

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const headers = new Headers(options?.headers);
  if (!(options?.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  const accessToken = getAccessToken();
  if (accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
    cache: "no-store",
  });

  if (!response.ok) {
    const responseText = await response.text();
    let message = responseText;
    try {
      const errorBody = JSON.parse(responseText) as { message?: string };
      message = errorBody.message || responseText;
    } catch {
      // The API can also return plain text errors.
    }
    if ((response.status === 401 || response.status === 403) && typeof window !== "undefined") {
      clearAuthSession();
      window.dispatchEvent(new Event(AUTH_UNAUTHORIZED_EVENT));
    }
    throw new Error(message || `API request failed: ${response.status}`);
  }

  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const apiClient = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: "POST", body: JSON.stringify(body) }),
  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "PATCH", body: body === undefined ? undefined : JSON.stringify(body) }),
  postForm: <T>(path: string, body: FormData) =>
    request<T>(path, { method: "POST", body }),
  delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};

export function resolveApiAssetUrl(path?: string): string | undefined {
  if (!path) return undefined;
  if (/^(https?:|data:|blob:)/i.test(path)) return path;
  return `${API_ORIGIN}/${path.replace(/^\/+/, "")}`;
}

export function toApiId(id: string): number {
  const numericId = Number(id.replace(/\D/g, ""));
  if (!Number.isInteger(numericId)) throw new Error(`Invalid API id: ${id}`);
  return numericId;
}
