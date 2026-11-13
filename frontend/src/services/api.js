function getBasicAuth() {
  return localStorage.getItem("basicAuth");
}

function getUserId() {
  // geçici: login/register bağlanana kadar
  const raw = localStorage.getItem("userId");
  const n = Number(raw);
  return Number.isFinite(n) && n > 0 ? n : 1;
}

function buildHeaders({ auth }) {
  const headers = { "Content-Type": "application/json" };
  if (auth) {
    const basic = getBasicAuth();
    if (basic) headers.Authorization = `Basic ${basic}`;
  }
  return headers;
}

async function safeReadBody(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

async function request(path, { method = "GET", body, auth = false } = {}) {
  const res = await fetch(path, {
    method,
    headers: buildHeaders({ auth }),
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const data = await safeReadBody(res);

  if (!res.ok) {
    const msg =
      (data && typeof data === "object" && (data.message || data.error)) ||
      (typeof data === "string" && data) ||
      `HTTP ${res.status}`;
    const err = new Error(msg);
    err.status = res.status;
    err.data = data;
    throw err;
  }

  return data;
}

function qs(params) {
  const u = new URLSearchParams();
  Object.entries(params || {}).forEach(([k, v]) => {
    if (v === undefined || v === null || v === "") return;
    u.set(k, String(v));
  });
  const s = u.toString();
  return s ? `?${s}` : "";
}

// -------- Tournaments (Public list + upcoming) --------

// PDF’de main page upcoming + search + paging olduğu için bu endpoint en doğru olan:
export function getUpcomingTournaments({ q = "", city = "", sportType = "", page = 0, size = 10 } = {}) {
  return request(`/api/tournaments/upcoming${qs({ q, city, sportType, page, size })}`, { auth: false });
}

export function getTournaments({ q = "", city = "", sportType = "", status = "", page = 0, size = 10 } = {}) {
  return request(`/api/tournaments${qs({ q, city, sportType, status, page, size })}`, { auth: false });
}

export function getTournamentById(id) {
  return request(`/api/tournaments/${id}`, { auth: false });
}

export function createTournament(payload) {
  return request("/api/tournaments", { method: "POST", body: payload, auth: true });
}

// -------- Matches (Req 8-9) --------

export function getTournamentMatches(tournamentId) {
  return request(`/api/tournaments/${tournamentId}/matches`, { auth: false });
}

export function submitMatchResult(matchId, winnerUserId) {
  return request(`/api/matches/${matchId}/result`, {
    method: "POST",
    body: { winnerUserId },
    auth: true,
  });
}

// -------- My Upcoming (requires userId param) --------

export function getMyUpcoming({ page = 0, size = 10 } = {}) {
  return request(`/api/tournaments/my-upcoming${qs({ page, size })}`, { auth: true });
}

// -------- Apply (requires userId param) --------

export function applyToTournament(id, payload) {
  return request(`/api/tournaments/${id}/apply`, {
    method: "POST",
    body: payload,
    auth: true,
  });
}

export function seedTournament(id) {
  return request(`/api/tournaments/${id}/seed`, { method: "POST", auth: true });
}

export function getMyUpcomingGames() {
  return request(`/api/tournaments/my-upcoming-games`, { auth: true });
}

// -------- Auth placeholders --------

export function login(payload) {
  return request("/api/auth/login", { method: "POST", body: payload, auth: false });
}

export function register(payload) {
  return request("/api/auth/register", { method: "POST", body: payload, auth: false });
}

export function setTokenFromAuthResponse(data) {
  // Backend token dönmüyor. Bu fonksiyon artık kullanılmamalı.
  throw new Error("Token-based auth is not used. Use setBasicAuthFromLogin instead.");
}

export function setBasicAuthFromLogin(email, password, loginResponse) {
  const basic = btoa(`${email}:${password}`);
  localStorage.setItem("basicAuth", basic);
  const uid = loginResponse?.id;
  if (uid) localStorage.setItem("userId", String(uid));
  return basic;
}

export function logout() {
  localStorage.removeItem("basicAuth");
  localStorage.removeItem("userId");
}
