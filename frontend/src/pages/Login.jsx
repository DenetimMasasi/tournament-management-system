import React, { useState } from "react";
import { login, setBasicAuthFromLogin } from "../services/api.js";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    try {
      setErr("");
      setMsg("");
      const data = await login({ email, password });
      setBasicAuthFromLogin(email, password, data);
      setMsg("Logged in successfully");
    } catch (e2) {
      setErr(e2.message || "Login failed");
    }
  }

  return (
    <div style={{ maxWidth: 420 }}>
      <h3 style={{ marginTop: 0 }}>Login</h3>
      <form onSubmit={onSubmit} style={{ display: "grid", gap: 10 }}>
        <input
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          placeholder="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button>Login</button>
        {msg ? <div style={{ color: "limegreen" }}>{msg}</div> : null}
        {err ? <div style={{ color: "crimson" }}>{err}</div> : null}
      </form>
    </div>
  );
}
