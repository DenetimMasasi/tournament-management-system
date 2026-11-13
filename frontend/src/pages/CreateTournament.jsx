import React, { useMemo, useState } from "react";
import { createTournament } from "../services/api.js";

const SPORT_TYPES = ["FOOTBALL", "BASKETBALL", "VOLLEYBALL", "TENNIS", "CHESS"];

export default function CreateTournament() {
  const today = useMemo(() => new Date().toISOString().slice(0, 10), []);

  const [form, setForm] = useState({
    name: "",
    city: "",
    startDate: "",
    endDate: "",
    applicationDeadline: "",
    maxTeams: 16,
    sportType: "FOOTBALL",
    rankedPlayers: 4,
    description: "",
    googleMapsUrl: "",
    sponsorLogos: "", 
  });

  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  function setField(key, value) {
    setForm((p) => ({ ...p, [key]: value }));
  }

  function validate() {
    if (!form.name.trim()) return "Name is required.";
    if (!form.city.trim()) return "City is required.";
    if (!form.startDate) return "Start date is required.";
    if (!form.endDate) return "End date is required.";
    if (!form.applicationDeadline) return "Application deadline is required.";
    if (form.endDate < form.startDate) return "End date cannot be before start date.";
    if (form.applicationDeadline > form.startDate)
      return "Application deadline should be on/before start date.";
    if (Number(form.maxTeams) < 2) return "Max teams must be at least 2.";
    if (!form.sportType) return "Sport type is required.";
    return "";
  }

  async function onSubmit(e) {
    e.preventDefault();

    const v = validate();
    if (v) {
      setErr(v);
      setMsg("");
      return;
    }

    try {
      setErr("");
      setMsg("");
      setSaving(true);

      const payload = {
        name: form.name.trim(),
        city: form.city.trim(),
        startDate: form.startDate, 
        endDate: form.endDate, 
        applicationDeadline: form.applicationDeadline, 
        maxTeams: Number(form.maxTeams),
        sportType: form.sportType,
        rankedPlayers: Number(form.rankedPlayers) || 0,
        description: form.description.trim() || null,
        googleMapsUrl: form.googleMapsUrl.trim() || null,
        sponsorLogos: form.sponsorLogos.trim() || null, 
      };

      await createTournament(payload);

      setMsg("Tournament created ✅");
      setForm({
        name: "",
        city: "",
        startDate: "",
        endDate: "",
        applicationDeadline: "",
        maxTeams: 16,
        sportType: "FOOTBALL",
        rankedPlayers: 4,
        description: "",
        googleMapsUrl: "",
        sponsorLogos: "",
      });
    } catch (e2) {
      setErr(e2.message || "Create failed");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <h3 style={{ marginTop: 0 }}>Create Tournament</h3>

      <form onSubmit={onSubmit} style={{ display: "grid", gap: 12, maxWidth: 720 }}>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          <label>
            Name *
            <input
              value={form.name}
              onChange={(e) => setField("name", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>

          <label>
            City *
            <input
              value={form.city}
              onChange={(e) => setField("city", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12 }}>
          <label>
            Start Date *
            <input
              type="date"
              value={form.startDate}
              min={today}
              onChange={(e) => setField("startDate", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>

          <label>
            End Date *
            <input
              type="date"
              value={form.endDate}
              min={form.startDate || today}
              onChange={(e) => setField("endDate", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>

          <label>
            Application Deadline *
            <input
              type="date"
              value={form.applicationDeadline}
              min={today}
              max={form.startDate || undefined}
              onChange={(e) => setField("applicationDeadline", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12 }}>
          <label>
            Max Teams *
            <input
              type="number"
              min="2"
              value={form.maxTeams}
              onChange={(e) => setField("maxTeams", e.target.value)}
              required
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>

          <label>
            Sport Type *
            <select
              value={form.sportType}
              onChange={(e) => setField("sportType", e.target.value)}
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            >
              {SPORT_TYPES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </label>

          <label>
            Ranked Players
            <input
              type="number"
              min="0"
              value={form.rankedPlayers}
              onChange={(e) => setField("rankedPlayers", e.target.value)}
              style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
            />
          </label>
        </div>

        <label>
          Description
          <textarea
            value={form.description}
            onChange={(e) => setField("description", e.target.value)}
            rows={4}
            style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
          />
        </label>

        <label>
          Google Maps URL
          <input
            value={form.googleMapsUrl}
            onChange={(e) => setField("googleMapsUrl", e.target.value)}
            placeholder="https://www.google.com/maps/search/?api=1&query=Poznan"
            style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
          />
        </label>

        <label>
          Sponsor Logos (comma separated URLs)
          <input
            value={form.sponsorLogos}
            onChange={(e) => setField("sponsorLogos", e.target.value)}
            placeholder="https://a.com/logo1.png,https://b.com/logo2.png"
            style={{ width: "100%", padding: 10, borderRadius: 10, border: "1px solid #2a2a2a" }}
          />
        </label>

        <button
          disabled={saving}
          style={{
            padding: 12,
            borderRadius: 12,
            border: "1px solid #2a2a2a",
            cursor: saving ? "not-allowed" : "pointer",
          }}
        >
          {saving ? "Creating..." : "Create"}
        </button>

        {msg ? <div style={{ color: "limegreen" }}>{msg}</div> : null}
        {err ? <div style={{ color: "crimson" }}>{err}</div> : null}
      </form>
    </div>
  );
}
