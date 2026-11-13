import React from "react";

function Match({ a, b, winner }) {
  return (
    <div
      style={{
        border: "1px solid #2a2a2a",
        borderRadius: 10,
        padding: 10,
        background: "#151515",
        width: 220,
      }}
    >
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <span style={{ opacity: winner === a ? 1 : 0.7 }}>{a || "TBD"}</span>
        {winner === a ? <b>✓</b> : null}
      </div>
      <div style={{ height: 6 }} />
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <span style={{ opacity: winner === b ? 1 : 0.7 }}>{b || "TBD"}</span>
        {winner === b ? <b>✓</b> : null}
      </div>
    </div>
  );
}

export default function BracketView({ rounds }) {
  return (
    <div
      style={{
        display: "flex",
        gap: 30,
        overflowX: "auto",
        paddingBottom: 10,
      }}
    >
      {rounds.map((round, i) => (
        <div key={i} style={{ display: "grid", gap: 16 }}>
          <div style={{ fontWeight: 700, textAlign: "center" }}>
            {round.title}
          </div>

          {round.matches.map((m, j) => (
            <Match
              key={j}
              a={m.a}
              b={m.b}
              winner={m.winner}
            />
          ))}
        </div>
      ))}
    </div>
  );
}
