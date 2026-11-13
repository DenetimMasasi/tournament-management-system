import React, { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { getMyUpcoming } from "../services/api.js";

export default function MyUpcoming() {
  const [sp, setSp] = useSearchParams();
  const page = Math.max(1, Number(sp.get("page") || 1));
  const size = 10;

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const items = useMemo(
    () => (Array.isArray(data?.content) ? data.content : []),
    [data]
  );

  const totalPages = Math.max(1, Number(data?.totalPages || 1));
  const safePage = Math.min(page, totalPages);

  useEffect(() => {
    (async () => {
      try {
        setErr("");
        setLoading(true);
        const resp = await getMyUpcoming({ page: safePage - 1, size });
        setData(resp);
      } catch (e) {
        setErr(e.message || "Failed to load");
      } finally {
        setLoading(false);
      }
    })();
  }, [safePage]);

  function goPage(p) {
    setSp((prev) => {
      const n = new URLSearchParams(prev);
      n.set("page", String(p));
      return n;
    });
  }

  if (loading) return <div>Loading...</div>;
  if (err) return <div style={{ color: "crimson" }}>{err}</div>;

  return (
    <div>
      <h3 style={{ marginTop: 0 }}>My Upcoming</h3>

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
                <div style={{ fontWeight: 700 }}>{t.name}</div>
                <div style={{ opacity: 0.8 }}>{t.sportType}</div>
              </div>

              <div style={{ opacity: 0.85, marginTop: 6 }}>
                City: {t.city} • {t.startDate} → {t.endDate}
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
        >
          Prev
        </button>
        <div>
          Page {safePage} / {totalPages}
        </div>
        <button
          onClick={() => goPage(Math.min(totalPages, safePage + 1))}
          disabled={safePage >= totalPages}
        >
          Next
        </button>
      </div>
    </div>
  );
}
