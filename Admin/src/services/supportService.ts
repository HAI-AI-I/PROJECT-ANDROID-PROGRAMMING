import { initialSupportRequests } from "@/data/support";
import type { SupportRequest } from "@/types/SupportRequest";

let store: SupportRequest[] = [...initialSupportRequests];
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

export const supportService = {
  async getRequests(): Promise<SupportRequest[]> {
    await delay();
    return [...store];
  },

  async getRequestById(id: string): Promise<SupportRequest | null> {
    await delay();
    return store.find((r) => r.id === id) ?? null;
  },

  async reply(id: string, reply: string): Promise<SupportRequest | null> {
    await delay();
    const idx = store.findIndex((r) => r.id === id);
    if (idx === -1) return null;
    store[idx] = { ...store[idx], reply, status: "in_progress" };
    return store[idx];
  },

  async markResolved(id: string): Promise<SupportRequest | null> {
    await delay();
    const idx = store.findIndex((r) => r.id === id);
    if (idx === -1) return null;
    store[idx] = { ...store[idx], status: "resolved" };
    return store[idx];
  },

  async closeRequest(id: string): Promise<SupportRequest | null> {
    await delay();
    const idx = store.findIndex((r) => r.id === id);
    if (idx === -1) return null;
    store[idx] = { ...store[idx], status: "closed" };
    return store[idx];
  },
};
