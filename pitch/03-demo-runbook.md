# Demo Runbook — FairShare Commons
**The live demo is the heart of the pitch. Follow this exactly. Never debug on stage.**

---

## Pre-flight (do this 10–15 min before you present)

### 1. Start the backend WITH the AI key
```bash
cd <repo root>
source ./env.local && mvn spring-boot:run
```
- Wait for `Started DemoApplication`.
- Confirm: open `http://localhost:8080/api/projects` → you should see JSON.
- ⚠️ The multilingual demo **requires the live AI**. Make sure the Anthropic account has credits.

### 2. Start the frontend
```bash
cd frontend && bun dev      # (or: npm run dev)
```
- Note the port (8080 is taken, so it picks another — e.g. **8081**). Open it.

### 3. Pre-create two accounts (so you never fumble sign-up live)
- **RLO:** name `Turkana Solar Collective` · email `loop@turkana.test` · type **RLO**
- **Donor:** name `Lina Hartog` · email `lina@donor.test` · regions `Kenya` · focus `solar`

### 4. Open TWO browser tabs, both logged in
- **Tab 1 — RLO** → logged in as Turkana Solar Collective, on **My organization**
- **Tab 2 — Donor** → logged in as Lina Hartog, on **Find projects**

(Switching tabs is faster and safer than logging in/out on stage.)

### 5. Put the Swahili note on your clipboard
```
Sisi ni kikundi cha wanawake katika kambi. Hatuna umeme wa kushona nguo usiku. Tunahitaji mashine za sola na taa. Tuna wanawake 15.
```
*(Translation, for your own confidence: "We are a women's group in the camp. We have no electricity to sew clothes at night. We need solar machines and lights. We are 15 women.")*

### 6. Set the co-pilot "Profile language" dropdown
- **English** (default) — safest, donor-readable.
- Or **Nederlands** for the Dutch-jury flourish (output appears in Dutch). Your call — decide in rehearsal.

### 7. ⚠️ BACKUP — record the demo beforehand
- Screen-record the full 90-second flow once it's working, save it **locally** (not streamed).
- Also keep 3–4 screenshots.
- If wifi or the API dies, **play the recording** and narrate over it. The offline canned fallback is English-only and won't show the Swahili→profile magic — the recording is your real safety net.

---

## The live sequence (~90 seconds)

**TAB 1 — RLO ("I am Amara"):**
1. In the **AI Grant-Writing Co-Pilot**, paste the Swahili note.
2. Click **Polish with AI**. *(3–5s — pause, let them watch.)*
3. The polished profile appears. Say: *"kept the real numbers, invented nothing, in the donor's language."*
4. In **Publish as a live project**: tick **solar**, amount **18000**, location **Kakuma, Kenya**.
5. Click **Publish as project** → green confirmation: *"…is now live."*

**TAB 2 — Donor:**
6. Regions: **Kenya** · tick **solar** · click **Find projects**.
7. Point to the just-published project: **"Strong fit"** badge, and note it sits **above better-funded orgs** (equity layer).

**The line to land:** *"The project that didn't exist 90 seconds ago — found by fit, not by network."*

---

## If something breaks
| Problem | Do this |
|---|---|
| AI call hangs >8s | Keep talking; it times out to a fallback. If it's the demo input, **switch to the recording**. |
| Backend down | Don't restart on stage. **Switch to the recording**, narrate, move on. |
| Frontend won't load | **Switch to the recording.** |
| Anything else | Never debug live. Recording → finish the story → Q&A. |

**Golden rule:** the audience remembers a smooth story, not a live stack trace. The recording exists so you can stay calm.

---

## One-line launch reminder (tape this to the laptop)
```
Backend:  source ./env.local && mvn spring-boot:run
Frontend: cd frontend && bun dev
Tabs:     1=RLO (My organization)   2=Donor (Find projects)
Clipboard: the Swahili note
```
