import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import {
  findProjects,
  findDonors,
  suggestPortfolio,
  login,
  registerDonor,
  registerOrganization,
  polishNotes,
  type DonorMatchRequest,
  type ProjectMatchRequest,
  type MatchResult,
  type DonorMatchResult,
  type FitLabel,
  type VerificationStatus,
  type PortfolioAllocation,
} from "../lib/api";
import { actions, useStore, type PortfolioItem } from "../lib/store";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Kindred Impact — Solidarity infrastructure" },
      { name: "description", content: "Fair discovery for refugee-led organizations and aligned donors." },
    ],
  }),
  component: Root,
});

type Tab = "search" | "portfolio" | "rlo" | "account";

const BAR_COLORS = [
  "var(--bar-1)", "var(--bar-2)", "var(--bar-3)", "var(--bar-4)",
  "var(--bar-5)", "var(--bar-6)", "var(--bar-7)", "var(--bar-8)",
];
const fmtEur = (n: number) =>
  n >= 1000 ? `€${(n / 1000).toFixed(0)}k` : `€${n.toFixed(0)}`;

const FOCUS_OPTIONS = [
  "solar", "wind", "cookstoves", "biomass", "micro-grid",
  "off-grid", "healthcare", "refugees", "indigenous", "climate",
];

// ------------------------------------------------------------------
// Root — auth gate
// ------------------------------------------------------------------

function Root() {
  const store = useStore();
  if (!store.user) return <AuthScreen />;
  return <App />;
}

// ------------------------------------------------------------------
// Auth screen
// ------------------------------------------------------------------

type AuthMode = "login" | "donor" | "org";

function AuthScreen() {
  const [mode, setMode] = useState<AuthMode>("login");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const [email, setEmail] = useState("");

  const [dName, setDName] = useState("");
  const [dEmail, setDEmail] = useState("");
  const [dMin, setDMin] = useState("");
  const [dMax, setDMax] = useState("");
  const [dRegions, setDRegions] = useState("");
  const [dFocus, setDFocus] = useState<string[]>([]);

  const [oName, setOName] = useState("");
  const [oEmail, setOEmail] = useState("");
  const [oType, setOType] = useState<"NGO" | "RLO">("RLO");
  const [oDesc, setODesc] = useState("");

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true); setError("");
    try {
      actions.setUser(await login(email));
    } catch {
      setError("No account found for that email. Register first.");
    } finally { setLoading(false); }
  }

  async function handleDonorRegister(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true); setError("");
    try {
      actions.setUser(await registerDonor({
        name: dName, email: dEmail, donorType: "INDIVIDUAL",
        preferredRegions: dRegions.split(",").map(s => s.trim()).filter(Boolean),
        preferredEnergyFocus: dFocus,
        minGivingCapacityEur: dMin ? Number(dMin) : null,
        maxGivingCapacityEur: dMax ? Number(dMax) : null,
        requiresVouchedOnly: false,
      }));
    } catch {
      setError("Registration failed. That email may already be registered.");
    } finally { setLoading(false); }
  }

  async function handleOrgRegister(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true); setError("");
    try {
      actions.setUser(await registerOrganization({
        name: oName, contactEmail: oEmail, organizationType: oType, description: oDesc,
      }));
    } catch {
      setError("Registration failed. That email may already be registered.");
    } finally { setLoading(false); }
  }

  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", background: "var(--bg)" }}>
      <div style={{ width: "min(440px, calc(100vw - 2rem))", padding: "1.5rem", background: "var(--surface)", border: "1px solid var(--border)", borderRadius: "8px" }}>
        <div style={{ marginBottom: "1rem" }}>
          <span className="brand">Kindred Impact</span>
          <span className="brand-sub">solidarity infrastructure</span>
        </div>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1.25rem", flexWrap: "wrap" }}>
          {(["login", "donor", "org"] as AuthMode[]).map(m => (
            <button key={m} className={`btn small${mode === m ? "" : " secondary"}`} onClick={() => { setMode(m); setError(""); }}>
              {m === "login" ? "Log in" : m === "donor" ? "Join as donor" : "Join as RLO/NGO"}
            </button>
          ))}
        </div>

        {error && <p style={{ color: "var(--danger)", fontSize: "0.85rem", marginBottom: "0.75rem" }}>{error}</p>}

        {mode === "login" && (
          <form onSubmit={handleLogin} style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <FieldInput label="Email" type="email" value={email} onChange={setEmail} required />
            <button className="btn" type="submit" disabled={loading}>{loading ? "…" : "Log in"}</button>
          </form>
        )}

        {mode === "donor" && (
          <form onSubmit={handleDonorRegister} style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <FieldInput label="Full name" value={dName} onChange={setDName} required />
            <FieldInput label="Email" type="email" value={dEmail} onChange={setDEmail} required />
            <FieldInput label="Preferred regions (comma-separated)" value={dRegions} onChange={setDRegions} placeholder="Kenya, East Africa" />
            <FocusCheckboxes selected={dFocus} onChange={setDFocus} />
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
              <FieldInput label="Min giving €" type="number" value={dMin} onChange={setDMin} />
              <FieldInput label="Max giving €" type="number" value={dMax} onChange={setDMax} />
            </div>
            <button className="btn" type="submit" disabled={loading}>{loading ? "…" : "Create donor account"}</button>
          </form>
        )}

        {mode === "org" && (
          <form onSubmit={handleOrgRegister} style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <FieldInput label="Organization name" value={oName} onChange={setOName} required />
            <FieldInput label="Email" type="email" value={oEmail} onChange={setOEmail} required />
            <div>
              <span style={{ fontSize: "0.85rem", fontWeight: 600 }}>Type</span>
              <div style={{ display: "flex", gap: "1rem", marginTop: "0.4rem" }}>
                {(["RLO", "NGO"] as const).map(t => (
                  <label key={t} style={{ display: "flex", alignItems: "center", gap: "0.3rem", fontSize: "0.85rem", cursor: "pointer" }}>
                    <input type="radio" checked={oType === t} onChange={() => setOType(t)} /> {t}
                  </label>
                ))}
              </div>
            </div>
            <div>
              <span style={{ fontSize: "0.85rem", fontWeight: 600 }}>One-sentence mission</span>
              <textarea value={oDesc} onChange={e => setODesc(e.target.value)}
                placeholder="We bring solar energy to refugee camps in East Africa."
                style={{ display: "block", width: "100%", marginTop: "0.25rem", padding: "0.5rem", border: "1px solid var(--border)", borderRadius: "4px", font: "inherit", boxSizing: "border-box", minHeight: "64px" }} />
            </div>
            <button className="btn" type="submit" disabled={loading}>{loading ? "…" : "Create organization account"}</button>
          </form>
        )}
      </div>
    </div>
  );
}

// ------------------------------------------------------------------
// Main app shell
// ------------------------------------------------------------------

function App() {
  const store = useStore();
  const defaultTab: Tab = store.user?.role === "ORGANIZATION" ? "rlo" : "search";
  const [tab, setTab] = useState<Tab>(defaultTab);

  return (
    <>
      <header className="app">
        <div className="container app-bar">
          <div>
            <span className="brand">Kindred Impact</span>
            <span className="brand-sub">solidarity infrastructure</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
            <span className="match-note">{store.user?.name} · {store.user?.role === "ORGANIZATION" ? "Organization" : "Donor"}</span>
            <button className="btn small secondary" onClick={() => actions.logout()}>Log out</button>
          </div>
        </div>
        <div className="container">
          <nav className="tabs" aria-label="Sections">
            <button className={tab === "search" ? "active" : ""} onClick={() => setTab("search")}>Find projects</button>
            <button className={tab === "portfolio" ? "active" : ""} onClick={() => setTab("portfolio")}>
              Portfolio {store.portfolio.length > 0 && <span className="badge">{store.portfolio.length}</span>}
            </button>
            <button className={tab === "rlo" ? "active" : ""} onClick={() => setTab("rlo")}>For RLOs</button>
            <button className={tab === "account" ? "active" : ""} onClick={() => setTab("account")}>Account</button>
          </nav>
        </div>
      </header>
      <main>
        <div className="container">
          {tab === "search" && <SearchTab />}
          {tab === "portfolio" && <PortfolioTab />}
          {tab === "rlo" && <RLOTab />}
          {tab === "account" && <AccountTab />}
        </div>
      </main>
    </>
  );
}

// ------------------------------------------------------------------
// Search tab — donor blind search
// ------------------------------------------------------------------

function SearchTab() {
  const store = useStore();
  const inPortfolio = new Set(store.portfolio.map(p => p.projectId));

  const [regions, setRegions] = useState("");
  const [focus, setFocus] = useState<string[]>([]);
  const [minGiving, setMinGiving] = useState("");
  const [maxGiving, setMaxGiving] = useState("");
  const [vouchedOnly, setVouchedOnly] = useState(false);
  const [results, setResults] = useState<MatchResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [searched, setSearched] = useState(false);

  async function handleSearch(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true); setError(""); setSearched(true);
    try {
      const criteria: DonorMatchRequest = {
        preferredRegions: regions.split(",").map(s => s.trim()).filter(Boolean),
        preferredEnergyFocus: focus,
        minGivingCapacityEur: minGiving ? Number(minGiving) : null,
        maxGivingCapacityEur: maxGiving ? Number(maxGiving) : null,
        requiresVouchedOnly: vouchedOnly,
      };
      setResults(await findProjects(criteria));
    } catch {
      setError("Could not reach the matching service. Is the backend running on port 8080?");
    } finally { setLoading(false); }
  }

  return (
    <section>
      <h1>Find projects</h1>
      <p className="help">
        Declare what you care about — you'll see matched projects before any organization names appear to avoid bias toward well-known groups.
      </p>

      <form onSubmit={handleSearch} className="card" style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
        <FieldInput label="Regions (comma-separated)" value={regions} onChange={setRegions} placeholder="Kenya, East Africa, Africa" />
        <FocusCheckboxes selected={focus} onChange={setFocus} />
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
          <FieldInput label="Min giving €" type="number" value={minGiving} onChange={setMinGiving} placeholder="5000" />
          <FieldInput label="Max giving €" type="number" value={maxGiving} onChange={setMaxGiving} placeholder="50000" />
        </div>
        <label style={{ display: "flex", alignItems: "center", gap: "0.4rem", fontSize: "0.85rem", cursor: "pointer" }}>
          <input type="checkbox" checked={vouchedOnly} onChange={e => setVouchedOnly(e.target.checked)} />
          Show vouched organizations only
        </label>
        <button className="btn" type="submit" disabled={loading} style={{ alignSelf: "flex-start" }}>
          {loading ? "Matching…" : "Find projects"}
        </button>
      </form>

      {error && <p style={{ color: "var(--danger)", fontSize: "0.85rem" }}>{error}</p>}

      {searched && !loading && (
        <div className="legend" style={{ marginTop: "0.75rem" }}>
          <span>{results.length} project{results.length !== 1 ? "s" : ""} matched</span>
          {results.length > 0 && <span>Sorted by fit · under-funded orgs surface first</span>}
        </div>
      )}

      {results.map(r => (
        <MatchResultCard key={r.projectId} result={r} inPortfolio={inPortfolio.has(r.projectId)} />
      ))}

      {searched && !loading && results.length === 0 && (
        <div className="empty">No matches. Try broader criteria — fewer regions or more focus areas.</div>
      )}
    </section>
  );
}

// ------------------------------------------------------------------
// Match result card
// ------------------------------------------------------------------

function MatchResultCard({ result, inPortfolio }: { result: MatchResult; inPortfolio: boolean }) {
  const funded = result.currentFundingAmountEur ?? 0;
  const target = result.targetAmountEur ?? 0;
  const pct = target > 0 ? Math.min(100, Math.round((funded / target) * 100)) : 0;

  return (
    <article className="card">
      <div className="card-head">
        <div style={{ flex: 1 }}>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", flexWrap: "wrap", marginBottom: "0.2rem" }}>
            <h2 style={{ margin: 0 }}>{result.projectTitle}</h2>
            <FitBadge label={result.fitLabel} />
            <VerificationBadge status={result.verificationStatus} />
          </div>
          <div className="card-meta">{result.organizationName} · {result.displayLocation}</div>
          {result.oneSentenceMission && <p style={{ fontSize: "0.88rem", margin: "0 0 0.4rem" }}>{result.oneSentenceMission}</p>}
        </div>
        <div>
          {inPortfolio ? (
            <button className="btn secondary small" onClick={() => actions.removeFromPortfolio(result.projectId)}>
              In portfolio · remove
            </button>
          ) : (
            <button className="btn small" onClick={() => actions.addToPortfolio({
              projectId: result.projectId,
              projectTitle: result.projectTitle,
              organizationName: result.organizationName,
              displayLocation: result.displayLocation,
            })}>
              Add to portfolio
            </button>
          )}
        </div>
      </div>

      <p style={{ fontSize: "0.85rem", color: "var(--muted)", margin: "0.25rem 0" }}>{result.matchReason}</p>

      {target > 0 && (
        <div style={{ margin: "0.5rem 0" }}>
          <div style={{ height: "6px", background: "var(--border)", borderRadius: "3px", overflow: "hidden" }}>
            <div style={{ height: "100%", width: `${pct}%`, background: "var(--micro)", borderRadius: "3px" }} />
          </div>
          <div className="match-note" style={{ marginTop: "0.2rem" }}>
            €{funded.toLocaleString()} of €{target.toLocaleString()} funded ({pct}%)
          </div>
        </div>
      )}

      <div className="tags">
        {result.energyFocusTags?.map(t => <span key={t} className="chip">{t}</span>)}
        {result.vouchedBy?.length > 0 && (
          <span className="chip" style={{ background: "#e8f0fe", borderColor: "#b3c4f5" }}>
            vouched by {result.vouchedBy.join(", ")}
          </span>
        )}
      </div>
    </article>
  );
}

function FitBadge({ label }: { label: FitLabel }) {
  const styles: Record<FitLabel, { bg: string; color: string }> = {
    "Strong fit":        { bg: "#e6f4ea", color: "#1f7a3a" },
    "Good fit":          { bg: "#e8f0fe", color: "#1c4a7a" },
    "Complementary fit": { bg: "#f3f3f0", color: "#6b6b66" },
  };
  const s = styles[label];
  return (
    <span style={{ fontSize: "0.75rem", fontWeight: 600, padding: "0.1rem 0.45rem", borderRadius: "3px", background: s.bg, color: s.color }}>
      {label}
    </span>
  );
}

function VerificationBadge({ status }: { status: VerificationStatus }) {
  if (status === "UNVERIFIED") return null;
  return (
    <span style={{ fontSize: "0.72rem", color: status === "FULLY_VERIFIED" ? "#1f7a3a" : "#1c4a7a", fontWeight: 600 }}>
      {status === "FULLY_VERIFIED" ? "✓ verified" : "✓ vouched"}
    </span>
  );
}

// ------------------------------------------------------------------
// Portfolio tab
// ------------------------------------------------------------------

function PortfolioTab() {
  const store = useStore();
  const [amount, setAmount] = useState("200");
  const [regions, setRegions] = useState("");
  const [focus, setFocus] = useState<string[]>([]);
  const [suggesting, setSuggesting] = useState(false);
  const [suggestionMsg, setSuggestionMsg] = useState("");
  const [lastBreakdown, setLastBreakdown] = useState<{ title: string; euros: number }[] | null>(null);

  const total = store.portfolio.reduce((s, p) => s + p.percent, 0);
  const amountNum = Math.max(0, Number(amount) || 0);

  async function handleAutoSuggest() {
    if (!amountNum) return;
    setSuggesting(true); setSuggestionMsg("");
    try {
      const criteria: DonorMatchRequest = {
        preferredRegions: regions.split(",").map(s => s.trim()).filter(Boolean),
        preferredEnergyFocus: focus,
        minGivingCapacityEur: null,
        maxGivingCapacityEur: null,
        requiresVouchedOnly: false,
      };
      const suggestion = await suggestPortfolio(criteria, amountNum);
      const items: PortfolioItem[] = suggestion.allocations.map((a: PortfolioAllocation) => ({
        projectId: a.projectId,
        projectTitle: a.projectTitle,
        organizationName: a.organizationName,
        displayLocation: a.displayLocation,
        percent: Math.round((a.suggestedAmountEur / suggestion.totalDonationEur) * 100),
      }));
      actions.setPortfolioFromSuggestion(items);
      setSuggestionMsg(suggestion.summary);
    } catch {
      setSuggestionMsg("Could not reach the matching service.");
    } finally { setSuggesting(false); }
  }

  if (store.portfolio.length === 0) {
    return (
      <section>
        <h1>Your portfolio</h1>
        <p className="help">Add projects from <strong>Find projects</strong>, or auto-suggest a gap-weighted basket below.</p>
        <div className="card">
          <h2>Auto-suggest a portfolio</h2>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <FieldInput label="Regions (optional)" value={regions} onChange={setRegions} placeholder="Africa, East Africa" />
            <FocusCheckboxes selected={focus} onChange={setFocus} />
            <div style={{ display: "flex", gap: "0.5rem", alignItems: "center", flexWrap: "wrap" }}>
              <label style={{ fontSize: "0.85rem" }}>Total donation €
                <input type="number" value={amount} onChange={e => setAmount(e.target.value)} min={1}
                  style={{ marginLeft: "0.5rem", width: "100px", padding: "0.35rem 0.5rem", border: "1px solid var(--border)", borderRadius: "4px", font: "inherit" }} />
              </label>
              <button className="btn" onClick={handleAutoSuggest} disabled={suggesting}>{suggesting ? "Building…" : "Auto-suggest"}</button>
            </div>
            {suggestionMsg && <p style={{ fontSize: "0.85rem", color: "var(--muted)" }}>{suggestionMsg}</p>}
          </div>
        </div>
      </section>
    );
  }

  return (
    <section>
      <h1>Your portfolio</h1>
      <p className="help">Adjust the split or donate as suggested. Allocation is weighted by remaining funding gap.</p>

      <div className="card">
        <div className="alloc-bar" aria-label="Allocation breakdown">
          {store.portfolio.map((p, i) =>
            p.percent > 0 ? (
              <div key={p.projectId} className="seg" style={{ width: `${p.percent}%`, background: BAR_COLORS[i % BAR_COLORS.length] }}
                title={`${p.projectTitle}: ${p.percent}%`} />
            ) : null
          )}
        </div>

        {store.portfolio.map((p, i) => (
          <div className="alloc-row" key={p.projectId}>
            <div>
              <span className="swatch" style={{ background: BAR_COLORS[i % BAR_COLORS.length] }} />
              <span className="name">{p.projectTitle}</span>{" "}
              <span className="match-note">· {p.organizationName} · {p.displayLocation}</span>
            </div>
            <div className="controls">
              <input type="range" min={0} max={100} value={p.percent} onChange={e => actions.setPercent(p.projectId, Number(e.target.value))} />
              <input type="number" min={0} max={100} value={p.percent} onChange={e => actions.setPercent(p.projectId, Number(e.target.value))} />
              <span>%</span>
            </div>
            <button className="btn danger small" onClick={() => actions.removeFromPortfolio(p.projectId)}>Remove</button>
          </div>
        ))}

        <div className="totals">
          <div>Total: <strong style={{ color: total === 100 ? "var(--micro)" : "var(--warn)" }}>{total}%</strong>
            {total !== 100 && <span className="match-note"> (must equal 100%)</span>}
          </div>
          <button className="btn secondary" onClick={() => actions.equalise()}>Equalise</button>
        </div>

        <div className="donate-row">
          <label htmlFor="amt">Amount €</label>
          <input id="amt" type="number" min={1} value={amount} onChange={e => setAmount(e.target.value)} />
          <button className="btn" disabled={total !== 100 || amountNum <= 0}
            onClick={() => {
              actions.donate(amountNum);
              setLastBreakdown(store.portfolio.map(p => ({ title: p.projectTitle, euros: Math.round(amountNum * p.percent) / 100 })));
            }}>
            Donate {fmtEur(amountNum)}
          </button>
        </div>

        {lastBreakdown && (
          <div style={{ marginTop: "1rem" }}>
            <h3>Donation confirmed</h3>
            <dl className="kv">
              {lastBreakdown.map(b => (
                <div key={b.title} style={{ display: "contents" }}>
                  <dt>{b.title}</dt><dd>€{b.euros.toFixed(2)}</dd>
                </div>
              ))}
            </dl>
          </div>
        )}
      </div>
    </section>
  );
}

// ------------------------------------------------------------------
// RLO tab — find donors + AI co-pilot
// ------------------------------------------------------------------

function RLOTab() {
  const [location, setLocation] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [targetAmount, setTargetAmount] = useState("");
  const [isVouched, setIsVouched] = useState(false);
  const [donorResults, setDonorResults] = useState<DonorMatchResult[]>([]);
  const [donorLoading, setDonorLoading] = useState(false);
  const [donorError, setDonorError] = useState("");
  const [donorSearched, setDonorSearched] = useState(false);

  const [rawNotes, setRawNotes] = useState("");
  const [polished, setPolished] = useState<{ problemStatement: string; beneficiaries: string; budget: string; sdgTags: string[] } | null>(null);
  const [polishing, setPolishing] = useState(false);
  const [polishError, setPolishError] = useState("");

  async function handleFindDonors(e: React.FormEvent) {
    e.preventDefault();
    setDonorLoading(true); setDonorError(""); setDonorSearched(true);
    try {
      setDonorResults(await findDonors({ location, energyFocusTags: tags, targetAmountEur: targetAmount ? Number(targetAmount) : null, isVouched } as ProjectMatchRequest));
    } catch {
      setDonorError("Could not reach the matching service. Is the backend running on port 8080?");
    } finally { setDonorLoading(false); }
  }

  async function handlePolish() {
    if (!rawNotes.trim()) return;
    setPolishing(true); setPolishError(""); setPolished(null);
    try {
      setPolished(await polishNotes({ rawNotes }));
    } catch {
      setPolishError("AI co-pilot is not yet available — backend endpoint coming soon.");
    } finally { setPolishing(false); }
  }

  return (
    <section>
      <h1>For RLOs &amp; NGOs</h1>
      <p className="help">Two tools to help your organization get discovered and funded.</p>

      <div className="card" style={{ marginBottom: "1.5rem" }}>
        <h2>Find aligned donors</h2>
        <p className="help">Describe your project — see donors whose criteria match, without revealing your organization name first.</p>
        <form onSubmit={handleFindDonors} style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
          <FieldInput label="Project location" value={location} onChange={setLocation} placeholder="Kakuma, Kenya" required />
          <FocusCheckboxes selected={tags} onChange={setTags} />
          <FieldInput label="Funding needed (€)" type="number" value={targetAmount} onChange={setTargetAmount} placeholder="25000" />
          <label style={{ display: "flex", alignItems: "center", gap: "0.4rem", fontSize: "0.85rem", cursor: "pointer" }}>
            <input type="checkbox" checked={isVouched} onChange={e => setIsVouched(e.target.checked)} />
            Our organization is vouched by an established NGO
          </label>
          <button className="btn" type="submit" disabled={donorLoading} style={{ alignSelf: "flex-start" }}>
            {donorLoading ? "Searching…" : "Find donors"}
          </button>
        </form>

        {donorError && <p style={{ color: "var(--danger)", fontSize: "0.85rem", marginTop: "0.5rem" }}>{donorError}</p>}

        {donorSearched && !donorLoading && (
          <div className="legend" style={{ marginTop: "0.75rem" }}>
            <span>{donorResults.length} donor{donorResults.length !== 1 ? "s" : ""} matched</span>
          </div>
        )}

        {donorResults.map(d => <DonorResultCard key={d.donorId} donor={d} />)}

        {donorSearched && !donorLoading && donorResults.length === 0 && (
          <div className="empty" style={{ marginTop: "0.5rem" }}>No donors matched. Try broader criteria.</div>
        )}
      </div>

      <div className="card">
        <h2>AI Grant-Writing Co-Pilot</h2>
        <p className="help">Type rough field notes in your own words — any language. The AI will turn them into a professional donor-ready profile.</p>
        <div style={{ background: "#fff3c4", padding: "0.5rem 0.75rem", borderRadius: "4px", borderLeft: "3px solid #e6cf6f", fontSize: "0.85rem", marginBottom: "0.75rem" }}>
          Example: <em>"we put solar on school, want second school, 200 kids, need money for panels and battery"</em>
        </div>
        <textarea value={rawNotes} onChange={e => setRawNotes(e.target.value)}
          placeholder="Describe your project in your own words..."
          style={{ width: "100%", minHeight: "100px", padding: "0.5rem", border: "1px solid var(--border)", borderRadius: "4px", font: "inherit", boxSizing: "border-box", resize: "vertical", marginBottom: "0.5rem" }} />
        <button className="btn" onClick={handlePolish} disabled={polishing || !rawNotes.trim()}>
          {polishing ? "Polishing…" : "Polish with AI"}
        </button>

        {polishError && <p style={{ color: "var(--warn)", fontSize: "0.85rem", marginTop: "0.5rem" }}>{polishError}</p>}

        {polished && (
          <div style={{ borderTop: "1px solid var(--border)", paddingTop: "0.75rem", marginTop: "0.75rem" }}>
            <h3>Your donor-ready profile</h3>
            <dl className="kv" style={{ marginTop: "0.5rem" }}>
              <dt>Problem</dt><dd>{polished.problemStatement}</dd>
              <dt>Beneficiaries</dt><dd>{polished.beneficiaries}</dd>
              <dt>Budget</dt><dd>{polished.budget}</dd>
              <dt>SDG tags</dt><dd>{polished.sdgTags?.join(", ")}</dd>
            </dl>
          </div>
        )}
      </div>
    </section>
  );
}

function DonorResultCard({ donor }: { donor: DonorMatchResult }) {
  return (
    <article className="card" style={{ margin: "0.5rem 0 0" }}>
      <div className="card-head">
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.2rem" }}>
            <h2 style={{ margin: 0 }}>{donor.donorName}</h2>
            <FitBadge label={donor.fitLabel} />
            <span style={{ fontSize: "0.75rem", color: "var(--muted)" }}>{donor.donorType}</span>
          </div>
          <div className="card-meta">
            {donor.preferredRegions?.join(", ")}
            {donor.minGivingCapacityEur && donor.maxGivingCapacityEur
              ? ` · €${donor.minGivingCapacityEur.toLocaleString()} – €${donor.maxGivingCapacityEur.toLocaleString()}`
              : ""}
          </div>
        </div>
      </div>
      <p style={{ fontSize: "0.85rem", color: "var(--muted)", margin: "0.2rem 0 0.4rem" }}>{donor.matchReason}</p>
      <div className="tags">
        {donor.preferredEnergyFocus?.map(t => <span key={t} className="chip">{t}</span>)}
      </div>
    </article>
  );
}

// ------------------------------------------------------------------
// Account tab
// ------------------------------------------------------------------

function AccountTab() {
  const store = useStore();
  const totalGiven = store.donations.reduce((s, d) => s + d.amount, 0);
  const last = store.donations[0];

  return (
    <section>
      <h1>My account</h1>
      <div className="card">
        <h2>Overview</h2>
        <dl className="kv">
          <dt>Name</dt><dd>{store.user?.name}</dd>
          <dt>Email</dt><dd>{store.user?.email}</dd>
          <dt>Role</dt><dd>{store.user?.role === "ORGANIZATION" ? "Organization / RLO" : "Donor"}</dd>
          <dt>Projects in portfolio</dt><dd>{store.portfolio.length}</dd>
          <dt>Total donated</dt><dd>€{totalGiven.toFixed(2)}</dd>
          <dt>Last donation</dt><dd>{last ? `€${last.amount.toFixed(2)} on ${new Date(last.date).toLocaleDateString()}` : "—"}</dd>
        </dl>
      </div>

      <div className="card">
        <h2>Donation history</h2>
        {store.donations.length === 0 ? (
          <div className="empty">No donations yet.</div>
        ) : store.donations.map(d => (
          <details key={d.id} style={{ marginBottom: "0.5rem" }}>
            <summary>
              {new Date(d.date).toLocaleString()} — <strong>€{d.amount.toFixed(2)}</strong> across {d.items.length} project{d.items.length !== 1 ? "s" : ""}
            </summary>
            <table className="history" style={{ marginTop: "0.5rem" }}>
              <thead><tr><th>Project</th><th>%</th><th>€</th></tr></thead>
              <tbody>
                {d.items.map(it => (
                  <tr key={it.projectId}><td>{it.projectTitle}</td><td>{it.percent}%</td><td>€{it.euros.toFixed(2)}</td></tr>
                ))}
              </tbody>
            </table>
          </details>
        ))}
      </div>

      <div className="card">
        <h2>Privacy</h2>
        <PrivToggle label="Anonymous donor mode" desc="Organizations see your donation but not your name or contact details." checked={store.privacy.anonymous} onChange={v => actions.setPrivacy({ anonymous: v })} />
        <PrivToggle label="Receive impact reports" desc="Periodic updates from the organizations you support." checked={store.privacy.impactReports} onChange={v => actions.setPrivacy({ impactReports: v })} />
        <PrivToggle label="Allow organizations to contact me" desc="Organizations may reach out about volunteer or matching opportunities." checked={store.privacy.contactOptIn} onChange={v => actions.setPrivacy({ contactOptIn: v })} />
      </div>
    </section>
  );
}

// ------------------------------------------------------------------
// Shared helpers
// ------------------------------------------------------------------

function FieldInput({ label, value, onChange, type = "text", placeholder = "", required = false }: {
  label: string; value: string; onChange: (v: string) => void;
  type?: string; placeholder?: string; required?: boolean;
}) {
  return (
    <label style={{ fontSize: "0.85rem", fontWeight: 600 }}>
      {label}
      <input type={type} value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder} required={required}
        style={{ display: "block", width: "100%", marginTop: "0.25rem", padding: "0.45rem 0.5rem", border: "1px solid var(--border)", borderRadius: "4px", font: "inherit", boxSizing: "border-box" }} />
    </label>
  );
}

function FocusCheckboxes({ selected, onChange }: { selected: string[]; onChange: (v: string[]) => void }) {
  return (
    <div>
      <span style={{ fontSize: "0.85rem", fontWeight: 600 }}>Energy focus</span>
      <div style={{ display: "flex", flexWrap: "wrap", gap: "0.4rem", marginTop: "0.4rem" }}>
        {FOCUS_OPTIONS.map(f => (
          <label key={f} style={{ display: "flex", alignItems: "center", gap: "0.25rem", fontSize: "0.82rem", cursor: "pointer" }}>
            <input type="checkbox" checked={selected.includes(f)}
              onChange={e => onChange(e.target.checked ? [...selected, f] : selected.filter(x => x !== f))} />
            {f}
          </label>
        ))}
      </div>
    </div>
  );
}

function PrivToggle({ label, desc, checked, onChange }: { label: string; desc: string; checked: boolean; onChange: (v: boolean) => void }) {
  return (
    <label className="priv-row">
      <input type="checkbox" checked={checked} onChange={e => onChange(e.target.checked)} style={{ marginTop: "0.2rem" }} />
      <div><div><strong>{label}</strong></div><div className="desc">{desc}</div></div>
    </label>
  );
}
