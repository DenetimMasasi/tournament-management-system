import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { http } from "../api/http";

export default function TournamentsPage() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  useEffect(() => {
    let alive = true;

    async function load() {
      setLoading(true);
      setErr("");
      try {
        const res = await http.get(`/api/tournaments/upcoming?page=${page}&size=${size}`);
        if (!alive) return;
        setItems(res.data.content ?? []);
      } catch (e) {
        if (!alive) return;
        setErr(e?.response?.data?.message || e.message || "Request failed");
      } finally {
        if (alive) setLoading(false);
      }
    }

    load();
    return () => {
      alive = false;
    };
  }, [page, size]);

  return (
    <div>
      <h3>Upcoming tournaments</h3>

      <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
        <button disabled={page === 0 || loading} onClick={() => setPage((p) => Math.max(0, p - 1))}>
          Prev
        </button>
        <button disabled={loading} onClick={() => setPage((p) => p + 1)}>
          Next
        </button>
        <span style={{ opacity: 0.7 }}>page: {page}</span>
      </div>

      {loading && <div>Loading…</div>}
      {err && <div style={{ color: "crimson" }}>{err}</div>}

      <div style={{ display: "grid", gap: 10 }}>
        {items.map((t) => (
          <div
            key={t.id}
            style={{
              border: "1px solid #333",
              borderRadius: 10,
              padding: 12,
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
              <div>
                <div style={{ fontWeight: 700 }}>{t.name}</div>
                <div style={{ opacity: 0.8 }}>{t.city}</div>
                <div style={{ opacity: 0.8 }}>
                  {t.startDate} → {t.endDate}
                </div>
                <div style={{ opacity: 0.8 }}>Deadline: {t.applicationDeadline}</div>
              </div>

              <div style={{ textAlign: "right" }}>
                <div style={{ fontWeight: 700 }}>{t.status}</div>
                <div style={{ opacity: 0.8 }}>maxTeams: {t.maxTeams}</div>
                <div style={{ marginTop: 8 }}>
                  <Link to={`/tournaments/${t.id}`}>Details</Link>
                </div>
              </div>
            </div>
          </div>
        ))}

        {!loading && items.length === 0 && <div>No upcoming tournaments found.</div>}
      </div>
    </div>
  );
}
