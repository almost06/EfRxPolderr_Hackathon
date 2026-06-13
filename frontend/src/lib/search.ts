import { MICRO_BUDGET_LIMIT, NGOS, type NGO } from "./ngos";

export type Match = { ngo: NGO; hits: number; isMicro: boolean };

// Seeded shuffle so randomisation within a tier is stable for a given query.
function shuffleSeeded<T>(arr: T[], seed: number): T[] {
  const a = arr.slice();
  let s = seed || 1;
  for (let i = a.length - 1; i > 0; i--) {
    s = (s * 9301 + 49297) % 233280;
    const j = Math.floor((s / 233280) * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function hashStr(s: string): number {
  let h = 0;
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) | 0;
  return Math.abs(h);
}

export function blindSearch(query: string): Match[] {
  const terms = query
    .toLowerCase()
    .split(/[\s,]+/)
    .map((t) => t.trim())
    .filter(Boolean);

  const scored: Match[] = NGOS.map((ngo) => {
    const haystack = [
      ngo.name.toLowerCase(),
      ngo.description.toLowerCase(),
      ngo.country.toLowerCase(),
      ...ngo.tags,
    ].join(" ");
    const hits = terms.length
      ? terms.reduce((n, t) => (haystack.includes(t) ? n + 1 : n), 0)
      : 0;
    return { ngo, hits, isMicro: ngo.budget < MICRO_BUDGET_LIMIT };
  });

  // If no query, return all in randomised order (still grouped: micro first).
  // Tiers: 0 = any hit, 1 = no hit (only shown if no query). When query present,
  // include only NGOs with at least one hit.
  const filtered = terms.length ? scored.filter((m) => m.hits > 0) : scored;

  // Group by hit-count tiers but interleave micro/large randomly inside each tier.
  const seed = hashStr(query || "all");
  const byTier = new Map<number, Match[]>();
  for (const m of filtered) {
    const tier = m.hits;
    if (!byTier.has(tier)) byTier.set(tier, []);
    byTier.get(tier)!.push(m);
  }
  const tiers = Array.from(byTier.keys()).sort((a, b) => b - a);
  const out: Match[] = [];
  for (const t of tiers) {
    out.push(...shuffleSeeded(byTier.get(t)!, seed + t));
  }
  return out;
}
