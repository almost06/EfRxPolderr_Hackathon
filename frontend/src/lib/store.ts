import { useEffect, useState } from "react";
import type { AuthUser } from "./api";

export type PortfolioItem = {
  projectId: number;
  projectTitle: string;
  organizationName: string;
  displayLocation: string;
  percent: number;
};

export type Donation = {
  id: string;
  date: string;
  amount: number;
  items: { projectId: number; projectTitle: string; percent: number; euros: number }[];
};

export type Privacy = {
  anonymous: boolean;
  impactReports: boolean;
  contactOptIn: boolean;
};

type State = {
  user: AuthUser | null;
  portfolio: PortfolioItem[];
  donations: Donation[];
  privacy: Privacy;
};

const KEY = "fairsharecommons:v1";

const DEFAULT: State = {
  user: null,
  portfolio: [],
  donations: [],
  privacy: { anonymous: false, impactReports: true, contactOptIn: false },
};

const listeners = new Set<() => void>();
let cached: State | null = null;

function read(): State {
  if (cached) return cached;
  if (typeof window === "undefined") return DEFAULT;
  try {
    const raw = localStorage.getItem(KEY);
    cached = raw ? { ...DEFAULT, ...JSON.parse(raw) } : DEFAULT;
  } catch {
    cached = DEFAULT;
  }
  return cached!;
}

function write(next: State) {
  cached = next;
  if (typeof window !== "undefined") {
    localStorage.setItem(KEY, JSON.stringify(next));
  }
  listeners.forEach((l) => l());
}

export function useStore() {
  const [state, setState] = useState<State>(() => read());
  useEffect(() => {
    const l = () => setState(read());
    listeners.add(l);
    return () => { listeners.delete(l); };
  }, []);
  return state;
}

export const actions = {
  setUser(user: AuthUser | null) {
    write({ ...read(), user });
  },
  logout() {
    write({ ...read(), user: null, portfolio: [] });
  },
  addToPortfolio(item: Omit<PortfolioItem, "percent">) {
    const s = read();
    if (s.portfolio.find((p) => p.projectId === item.projectId)) return;
    const next = [...s.portfolio, { ...item, percent: 0 }];
    write({ ...s, portfolio: equalise(next) });
  },
  removeFromPortfolio(projectId: number) {
    const s = read();
    write({ ...s, portfolio: s.portfolio.filter((p) => p.projectId !== projectId) });
  },
  setPercent(projectId: number, percent: number) {
    const s = read();
    write({
      ...s,
      portfolio: s.portfolio.map((p) =>
        p.projectId === projectId ? { ...p, percent: Math.max(0, Math.min(100, percent)) } : p,
      ),
    });
  },
  setPortfolioFromSuggestion(items: PortfolioItem[]) {
    write({ ...read(), portfolio: items });
  },
  equalise() {
    const s = read();
    write({ ...s, portfolio: equalise(s.portfolio) });
  },
  donate(amount: number) {
    const s = read();
    if (s.portfolio.length === 0 || amount <= 0) return;
    const items = s.portfolio.map((p) => ({
      projectId: p.projectId,
      projectTitle: p.projectTitle,
      percent: p.percent,
      euros: Math.round(amount * p.percent) / 100,
    }));
    const donation: Donation = {
      id: `d${Date.now()}`,
      date: new Date().toISOString(),
      amount,
      items,
    };
    write({ ...s, donations: [donation, ...s.donations] });
  },
  setPrivacy(patch: Partial<Privacy>) {
    const s = read();
    write({ ...s, privacy: { ...s.privacy, ...patch } });
  },
};

function equalise(items: PortfolioItem[]): PortfolioItem[] {
  if (items.length === 0) return items;
  const base = Math.floor(100 / items.length);
  const remainder = 100 - base * items.length;
  return items.map((p, i) => ({ ...p, percent: base + (i < remainder ? 1 : 0) }));
}
