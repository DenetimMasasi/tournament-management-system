import React, { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { getUpcomingTournaments } from "../services/api.js";

export default function HomePage() {
  const [sp, setSp] = useSearchParams();

  const q = sp.get("q") || "";
  const page = Math.max(1, Number(sp.get("page") || 1));
  const size = 10;

  const [data, setData] = useState(null); // Page<TournamentResponse>
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const items = useMemo(() => {
    if (!data) return [];
    return Array.isArray(data.content) ? data.content : [];
  }, [data]);

  const totalPages = useMemo(() => {
    const tp = Number(data?.totalPages);
    return Number.isFinite(tp) && tp > 0 ? tp : 1;
  }, [data]);

  const safePage = Math.min(page, totalPages);

  useEffect(() => {
    (async () => {
      try {
        setErr("");
        setLoading(true);

        const resp = await getUpcomingTournaments({
          q,
          page: safePage - 1, // backend 0-based
          size,
        });

        setData(resp);
      } catch (e) {
        setErr(e.message || "Failed to load");
      } finally {
        setLoading(false);
      }
    })();
  }, [q, safePage]);

  function setQuery(nextQ) {
    setSp((prev) => {
      const n = new URLSearchParams(prev);
      if (nextQ) n.set("q", nextQ);
      else n.delete("q");
      n.set("page", "1");
      return n;
    });
  }

  function goPage(nextPage) {
    setSp((prev) => {
      const n = new URLSearchParams(prev);
      n.set("page", String(nextPage));
      return n;
    });
  }

  if (loading) return <div>Loading...</div>;
  if (err) return <div style={{ color: "crimson" }}>{err}</div>;

  return (
    <div>
      <h3 style={{ marginTop: 0 }}>Upcoming Tournaments</h3>

      <div style={{ display: "flex", gap: 10, alignItems: "center", marginBottom: 14 }}>
        <input
          value={q}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search (name)..."
          style={{ flex: 1, padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
        />
        <div style={{ opacity: 0.85, whiteSpace: "nowrap" }}>
          {Number.isFinite(Number(data?.totalElements)) ? `${data.totalElements} total` : ""}
        </div>
      </div>

      {items.length === 0 ? (
        <div>No upcoming tournaments.</div>
      ) : (
        <div style={{ display: "grid", gap: 14 }}>
          {items.map((t) => (
            <div
              key={t.id}
              style={{
                border: "1px solid #2a2a2a",
                borderRadius: 12,
                padding: 14,
                background: "#151515",
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                <div style={{ fontWeight: 700, fontSize: 16 }}>{t.name}</div>
                <div style={{ opacity: 0.8 }}>{t.sportType}</div>
              </div>

              <div style={{ opacity: 0.85, marginTop: 6 }}>
                City: {t.city} • Start: {t.startDate} • End: {t.endDate}
              </div>

              <div style={{ marginTop: 10 }}>
                <Link to={`/tournaments/${t.id}`}>
                  <button
                    style={{
                      padding: "6px 14px",
                      borderRadius: 8,
                      border: "1px solid #2a2a2a",
                      background: "#1f1f1f",
                      color: "#fff",
                      cursor: "pointer",
                    }}
                  >
                    Details
                  </button>
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}

      <div style={{ display: "flex", gap: 10, alignItems: "center", marginTop: 16 }}>
        <button
          onClick={() => goPage(Math.max(1, safePage - 1))}
          disabled={safePage <= 1}
          style={{
            padding: "6px 12px",
            borderRadius: 8,
            border: "1px solid #2a2a2a",
            background: "#1f1f1f",
            color: "#fff",
            cursor: safePage <= 1 ? "not-allowed" : "pointer",
          }}
        >
          Prev
        </button>

        <div style={{ opacity: 0.9 }}>
          Page {safePage} / {totalPages}
        </div>

        <button
          onClick={() => goPage(Math.min(totalPages, safePage + 1))}
          disabled={safePage >= totalPages}
          style={{
            padding: "6px 12px",
            borderRadius: 8,
            border: "1px solid #2a2a2a",
            background: "#1f1f1f",
            color: "#fff",
            cursor: safePage >= totalPages ? "not-allowed" : "pointer",
          }}
        >
          Next
        </button>
      </div>
    </div>
  );
}
