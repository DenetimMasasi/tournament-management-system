import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  applyToTournament,
  getTournamentById,
  getTournamentMatches,
  getTournaments,
  seedTournament,
  submitMatchResult,
} from "../services/api.js";
import BracketView from "../components/BracketView.jsx";

function splitLogos(sponsorLogos) {
  if (!sponsorLogos) return [];
  return String(sponsorLogos)
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);
}

function parseDate(value) {
  if (!value) return null;
  const d = new Date(value);
  return Number.isFinite(d.getTime()) ? d : null;
}

export default function TournamentDetailsPage() {
  const { id } = useParams();

  const [tournament, setTournament] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const [tab, setTab] = useState("details");

  const [applyForm, setApplyForm] = useState({
    licenseNumber: "",
    ranking: "",
  });
  const [applying, setApplying] = useState(false);
  const [applyMsg, setApplyMsg] = useState("");
  const [applyErr, setApplyErr] = useState("");

  const [matches, setMatches] = useState([]);
  const [matchesLoading, setMatchesLoading] = useState(false);
  const [matchesErr, setMatchesErr] = useState("");
  const currentUserId = Number(localStorage.getItem("userId") || "0") || 0;

  const logos = useMemo(
    () => splitLogos(tournament?.sponsorLogos),
    [tournament?.sponsorLogos]
  );

  const loadMatches = useCallback(async () => {
    try {
      setMatchesErr("");
      setMatchesLoading(true);
      const data = await getTournamentMatches(id);
      setMatches(Array.isArray(data) ? data : []);
    } catch (e) {
      setMatchesErr(e?.message || "Failed to load matches");
    } finally {
      setMatchesLoading(false);
    }
  }, [id]);

  const rounds = useMemo(() => {
    const byRound = new Map();
    for (const m of matches) {
      const r = Number(m?.roundNo || 0);
      if (!byRound.has(r)) byRound.set(r, []);
      byRound.get(r).push(m);
    }

    const keys = [...byRound.keys()].sort((a, b) => a - b);
    const built = keys.map((r) => {
      const list = (byRound.get(r) || [])
        .slice()
        .sort((a, b) => Number(a?.matchNo || 0) - Number(b?.matchNo || 0));

      return {
        title: `Round ${r}`,
        matches: list.map((m) => {
          const a =
            m?.player1License ||
            (m?.player1UserId ? `User ${m.player1UserId}` : "TBD");
          const b =
            m?.player2License ||
            (m?.player2UserId ? `User ${m.player2UserId}` : "TBD");

          let winner = null;
          if (
            m?.confirmedWinnerUserId &&
            m?.player1UserId &&
            m.confirmedWinnerUserId === m.player1UserId
          ) {
            winner = a;
          }
          if (
            m?.confirmedWinnerUserId &&
            m?.player2UserId &&
            m.confirmedWinnerUserId === m.player2UserId
          ) {
            winner = b;
          }

          return { a, b, winner };
        }),
      };
    });

    return built;
  }, [matches]);

  useEffect(() => {
    (async () => {
      try {
        setErr("");
        setLoading(true);

        let t = null;
        try {
          t = await getTournamentById(id);
        } catch {
          t = null;
        }

        if (!t) {
          const data = await getTournaments({ page: 0, size: 200 });
          const list = Array.isArray(data?.content) ? data.content : [];
          t = list.find((x) => String(x.id) === String(id)) || null;
        }

        setTournament(t);
      } catch (e) {
        setErr(e?.message || "Failed to load tournament details");
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  useEffect(() => {
    if (tab === "ladder" || tab === "results") {
      loadMatches();
    }
  }, [tab, loadMatches]);

  async function onSeed() {
    try {
      await seedTournament(id);
      await loadMatches();
      alert("Tournament seeded ✅");
    } catch (e) {
      alert(e?.message || "Seed failed");
    }
  }

  function setApplyField(key, value) {
    setApplyForm((p) => ({ ...p, [key]: value }));
  }

  async function onApply(e) {
    e.preventDefault();
    setApplyErr("");
    setApplyMsg("");

    const license = applyForm.licenseNumber.trim();
    const ranking = Number(applyForm.ranking);

    if (!license) {
      setApplyErr("License number is required.");
      return;
    }
    if (!Number.isFinite(ranking) || ranking < 0) {
      setApplyErr("Ranking must be a number (0 or greater).");
      return;
    }

    try {
      setApplying(true);
      await applyToTournament(id, { licenseNumber: license, ranking });
      setApplyMsg("Application submitted ✅");
      setApplyForm({ licenseNumber: "", ranking: "" });
    } catch (e2) {
      setApplyErr(e2?.message || "Apply failed");
    } finally {
      setApplying(false);
    }
  }

  async function onSubmitResult(matchId, winnerUserId) {
    try {
      await submitMatchResult(matchId, winnerUserId);
      await loadMatches();
    } catch (e) {
      alert(e?.message || "Submit result failed");
    }
  }

  if (loading) return <div>Loading...</div>;
  if (err) return <div style={{ color: "crimson" }}>{err}</div>;
  if (!tournament) return <div>Not found</div>;

  const organizer = tournament.organizerName || "Unknown";
  const maxParticipants = tournament.maxParticipants ?? tournament.maxTeams ?? "—";
  const deadline = tournament.applicationDeadline || "—";

  const safeRounds = rounds.length
    ? rounds
    : [{ title: "No matches yet", matches: [] }];

  const organizerUserId =
    Number(tournament.organizerUserId || tournament.organizerId || 0) || 0;

  const isLoggedIn = Boolean(currentUserId);
  const isOrganizer =
    organizerUserId > 0 ? currentUserId === organizerUserId : false;

  const deadlineDate = parseDate(tournament.applicationDeadline);
  const now = new Date();
  const deadlinePassed = deadlineDate ? now > deadlineDate : false;

  const participantsNow =
    Number(tournament.participantsCount || tournament.currentParticipants || 0) ||
    0;
  const maxNow = Number(tournament.maxParticipants ?? tournament.maxTeams ?? 0) || 0;
  const isFull = maxNow > 0 ? participantsNow >= maxNow : false;

  const showApply = isLoggedIn && !isOrganizer && !deadlinePassed && !isFull;

  return (
    <div style={{ display: "grid", gap: 14 }}>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <div>
          <h3 style={{ margin: 0 }}>{tournament.name}</h3>
          <div style={{ opacity: 0.85 }}>
            Discipline: <b>{tournament.sportType}</b> • City:{" "}
            <b>{tournament.city}</b>
          </div>
        </div>
        <div style={{ textAlign: "right", opacity: 0.85 }}>
          <div>
            Status: <b>{tournament.status}</b>
          </div>
          <div>
            Ranked Players: <b>{tournament.rankedPlayers}</b>
          </div>
        </div>
      </div>

      <div style={{ display: "flex", gap: 10 }}>
        <button onClick={() => setTab("details")}>Details</button>
        <button onClick={() => setTab("ladder")}>Ladder</button>
        <button onClick={() => setTab("results")}>Results</button>
      </div>

      {tab === "details" && (
        <>
          <div
            style={{
              border: "1px solid #2a2a2a",
              borderRadius: 12,
              padding: 14,
              background: "#151515",
              display: "grid",
              gap: 8,
            }}
          >
            <div>
              Organizer: <b>{organizer}</b>
            </div>
            <div>
              Time: <b>{tournament.startDate}</b> → <b>{tournament.endDate}</b>
            </div>
            <div>
              Application deadline: <b>{deadline}</b>
            </div>
            <div>
              Max participants: <b>{maxParticipants}</b>
            </div>

            {tournament.googleMapsUrl ? (
              <div>
                Google Maps:{" "}
                <a
                  href={tournament.googleMapsUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  Open location
                </a>
              </div>
            ) : null}
          </div>

          {tournament.description ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
              }}
            >
              <b>Description</b>
              <div style={{ opacity: 0.9 }}>{tournament.description}</div>
            </div>
          ) : null}

          <div
            style={{
              border: "1px solid #2a2a2a",
              borderRadius: 12,
              padding: 14,
              background: "#151515",
            }}
          >
            <b>Sponsor logos</b>
            {logos.length === 0 ? (
              <div style={{ opacity: 0.8 }}>No sponsor logos.</div>
            ) : (
              <div style={{ display: "flex", gap: 10, marginTop: 10 }}>
                {logos.map((url) => (
                  <img
                    key={url}
                    src={url}
                    alt="sponsor"
                    style={{ width: 90, height: 50, objectFit: "contain" }}
                    onError={(e) => (e.currentTarget.style.display = "none")}
                  />
                ))}
              </div>
            )}
          </div>

          {!isLoggedIn ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
                opacity: 0.85,
              }}
            >
              Login to apply.
            </div>
          ) : isOrganizer ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
                opacity: 0.85,
              }}
            >
              Organizer cannot apply to their own tournament.
            </div>
          ) : deadlinePassed ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
                opacity: 0.85,
              }}
            >
              Applications are closed (deadline passed).
            </div>
          ) : isFull ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
                opacity: 0.85,
              }}
            >
              Applications are closed (tournament is full).
            </div>
          ) : null}

          {showApply ? (
            <div
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
              }}
            >
              <b>Apply</b>
              <form onSubmit={onApply} style={{ display: "grid", gap: 10 }}>
                <input
                  placeholder="License number"
                  value={applyForm.licenseNumber}
                  onChange={(e) =>
                    setApplyField("licenseNumber", e.target.value)
                  }
                />
                <input
                  type="number"
                  placeholder="Ranking"
                  value={applyForm.ranking}
                  onChange={(e) => setApplyField("ranking", e.target.value)}
                />
                <button disabled={applying}>
                  {applying ? "Applying..." : "Apply"}
                </button>
                {applyMsg ? (
                  <div style={{ color: "lime" }}>{applyMsg}</div>
                ) : null}
                {applyErr ? (
                  <div style={{ color: "crimson" }}>{applyErr}</div>
                ) : null}
              </form>
            </div>
          ) : null}
        </>
      )}

      {tab === "ladder" && (
        <div style={{ display: "grid", gap: 12 }}>
          <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
            <button onClick={onSeed}>Seed tournament</button>
            <button onClick={loadMatches} disabled={matchesLoading}>
              {matchesLoading ? "Refreshing..." : "Refresh"}
            </button>
            {matchesErr ? (
              <span style={{ color: "crimson" }}>{matchesErr}</span>
            ) : null}
          </div>

          <BracketView rounds={safeRounds} />
        </div>
      )}

      {tab === "results" && (
        <div style={{ display: "grid", gap: 12 }}>
          <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
            <button onClick={loadMatches} disabled={matchesLoading}>
              {matchesLoading ? "Refreshing..." : "Refresh"}
            </button>
            {matchesErr ? (
              <span style={{ color: "crimson" }}>{matchesErr}</span>
            ) : null}
          </div>

          <div
            style={{
              border: "1px solid #2a2a2a",
              borderRadius: 12,
              padding: 14,
              background: "#151515",
              display: "grid",
              gap: 12,
            }}
          >
            <h4 style={{ margin: 0 }}>Enter Match Results</h4>

            {matches
              .slice()
              .sort(
                (a, b) =>
                  (Number(a?.roundNo || 0) - Number(b?.roundNo || 0)) ||
                  (Number(a?.matchNo || 0) - Number(b?.matchNo || 0))
              )
              .map((m) => {
                const a =
                  m?.player1License ||
                  (m?.player1UserId ? `User ${m.player1UserId}` : "TBD");
                const b =
                  m?.player2License ||
                  (m?.player2UserId ? `User ${m.player2UserId}` : "TBD");
                const status = m?.status || "—";
                const isMine =
                  currentUserId &&
                  (currentUserId === m?.player1UserId ||
                    currentUserId === m?.player2UserId);

                const hasOpponent = Boolean(m?.player2UserId);
                const isCompleted = String(status).toUpperCase() === "COMPLETED";
                const canSubmit = Boolean(isMine && hasOpponent && !isCompleted);

                const confirmedText = m?.confirmedWinnerUserId
                  ? m.confirmedWinnerUserId === m?.player1UserId
                    ? a
                    : b
                  : null;

                return (
                  <div
                    key={m.id}
                    style={{
                      border: "1px solid #333",
                      borderRadius: 10,
                      padding: 12,
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        gap: 10,
                      }}
                    >
                      <div style={{ fontWeight: 700 }}>
                        Round {m?.roundNo} • Match {m?.matchNo}: {a} vs {b}
                      </div>
                      <div style={{ opacity: 0.85 }}>
                        Status: <b>{status}</b>
                      </div>
                    </div>

                    {confirmedText ? (
                      <div style={{ marginTop: 8, color: "limegreen" }}>
                        ✔ Confirmed winner: {confirmedText}
                      </div>
                    ) : null}

                    {!hasOpponent ? (
                      <div style={{ marginTop: 8, opacity: 0.8 }}>
                        BYE (no opponent)
                      </div>
                    ) : null}

                    {canSubmit ? (
                      <div
                        style={{
                          display: "flex",
                          gap: 10,
                          marginTop: 10,
                          flexWrap: "wrap",
                        }}
                      >
                        <button
                          onClick={() => onSubmitResult(m.id, m.player1UserId)}
                        >
                          Winner: {a}
                        </button>
                        <button
                          onClick={() => onSubmitResult(m.id, m.player2UserId)}
                        >
                          Winner: {b}
                        </button>
                        <div style={{ opacity: 0.75, alignSelf: "center" }}>
                          (Both players must submit the same winner.)
                        </div>
                      </div>
                    ) : (
                      <div style={{ marginTop: 10, opacity: 0.75 }}>
                        {currentUserId
                          ? "Login as a match participant to submit a result."
                          : "Login to submit results."}
                      </div>
                    )}
                  </div>
                );
              })}
          </div>
        </div>
      )}

      <Link to="/">← Back to Home</Link>
    </div>
  );
}
