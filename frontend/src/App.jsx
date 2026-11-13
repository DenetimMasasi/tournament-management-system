import React from "react";
import { Routes, Route, Link } from "react-router-dom";

import "./App.css";

import HomePage from "./pages/HomePage.jsx";
import MyUpcoming from "./pages/MyUpcoming.jsx";
import CreateTournament from "./pages/CreateTournament.jsx";
import TournamentDetailsPage from "./pages/TournamentDetailsPage.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";

export default function App() {
  return (
    <div className="container">
      <header className="topbar">
        <h2 style={{ margin: 0 }}>Tournament UI</h2>

        <nav className="nav">
          <Link to="/">Home</Link>
          <Link to="/my-upcoming">My Upcoming</Link>
          <Link to="/tournaments/create">Create Tournament</Link>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </nav>
      </header>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/my-upcoming" element={<MyUpcoming />} />
        <Route path="/tournaments/create" element={<CreateTournament />} />
        <Route path="/tournaments/:id" element={<TournamentDetailsPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="*" element={<div>404</div>} />
      </Routes>
    </div>
  );
}
