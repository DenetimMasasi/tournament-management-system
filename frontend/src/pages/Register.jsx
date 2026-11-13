import React, { useState } from "react";
import { register } from "../services/api.js";

export default function Register() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    try {
      setErr("");
      setMsg("");
      await register({ firstName, lastName, email, password });
      setMsg("Registered successfully. Please confirm your email, then login.");
      setFirstName("");
      setLastName("");
      setEmail("");
      setPassword("");
    } catch (e2) {
      setErr(e2.message || "Register failed");
    }
  }

  return (
    <div style={{ maxWidth: 420 }}>
      <h3 style={{ marginTop: 0 }}>Register</h3>
      <form onSubmit={onSubmit} style={{ display: "grid", gap: 10 }}>
        <input
          placeholder="First name"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
        />
        <input
          placeholder="Last name"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
        />
        <input
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          placeholder="Password (min 6 chars)"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button>Register</button>
        {msg ? <div style={{ color: "limegreen" }}>{msg}</div> : null}
        {err ? <div style={{ color: "crimson" }}>{err}</div> : null}
      </form>
    </div>
  );
}
