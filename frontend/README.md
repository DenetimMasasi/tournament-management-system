# Tournament Management System — Frontend

The frontend of the [Tournament Management System](../README.md) is a **React** single-page application built with **Vite**. It provides the interface for browsing tournaments, creating tournaments, registering as a participant, viewing brackets, and submitting match results through the Spring Boot API.

> **Repository history:** This application was developed before it was later published on GitHub. The initial repository commit reflects the code import, not the beginning of development.

## Technology Stack

- **React 19** for the interface
- **React Router 7** for client-side navigation
- **Vite 7** for development and production builds
- **JavaScript and CSS**
- **Fetch API** for active backend requests

The dependencies also include Axios, but the currently used API client is `src/services/api.js`, which is based on `fetch`.

## User-Facing Features

- **Home:** Browse and search upcoming tournaments by name, with paginated results.
- **Tournament details:** View tournament information, switch between details, ladder, and results, and inspect match data.
- **Registration and login:** Create an account and sign in using the backend's authentication endpoints.
- **Create tournament:** Provide tournament information through a form.
- **Apply to a tournament:** Join with a license number and ranking, subject to backend validation.
- **Bracket view:** Display the single-elimination ladder after the organizer seeds the tournament.
- **Result submission:** Match participants can submit their winner selections.
- **My Upcoming:** View upcoming tournaments associated with the signed-in user.

## Pages and Code Organization

```text
src/
├── App.jsx                     # Navigation and application routes
├── App.css
├── main.jsx                    # React entry point
├── index.css
├── pages/
│   ├── HomePage.jsx            # Upcoming tournament search and pagination
│   ├── TournamentDetailsPage.jsx # Details, applications, ladder, match results
│   ├── CreateTournament.jsx    # Tournament creation
│   ├── MyUpcoming.jsx          # Registered upcoming tournaments
│   ├── Login.jsx               # Sign-in
│   ├── Register.jsx            # Sign-up
│   └── TournamentsPage.jsx
├── components/
│   └── BracketView.jsx         # Bracket display
├── services/
│   └── api.js                  # Active fetch-based API client
└── api/
    └── http.js                 # Additional Axios helper (not active in main flow)
```

The routes currently defined in `App.jsx` are:

| Route | Page |
| --- | --- |
| `/` | Upcoming tournaments |
| `/tournaments/:id` | Tournament details, bracket, and results |
| `/tournaments/create` | Create tournament |
| `/my-upcoming` | My Upcoming |
| `/login` | Login |
| `/register` | Registration |

A separate page component exists for `TournamentsPage.jsx`, but it is not registered as a route in the current `App.jsx`.

## Run Locally

### Prerequisites

- Node.js and npm compatible with Vite 7
- The [Spring Boot backend](../backend/README.md), running at `http://localhost:8080`
- PostgreSQL configured for the backend

### 1. Install dependencies

From the `frontend/` directory:

```bash
npm install
```

### 2. Start the development server

```bash
npm run dev
```

Open **http://localhost:5173** (the default Vite port, if available).

The Vite development configuration proxies requests matching `/api` to `http://localhost:8080`. As a result, frontend requests can use relative paths, such as `/api/tournaments/upcoming`.

### 3. Use the application

1. Explore public tournament listings on the home page.
2. Register and confirm your email using the backend confirmation link.
3. Log in to enable tournament creation, applications, and personalized views.
4. Apply to a tournament or create one.
5. If you're the organizer, generate the bracket; if you're a participant, use the details page to view matches and submit results.

## Available Commands

| Command | Action |
| --- | --- |
| `npm run dev` | Start the Vite development server |
| `npm run build` | Build assets into `dist/` |
| `npm run preview` | Preview a production build locally |
| `npm run lint` | Run ESLint |

To check the frontend:

```bash
npm run lint
npm run build
```

These commands are documented from the project scripts; successful execution has not been independently verified.

## API Integration and Authentication

Most requests are implemented in `src/services/api.js` and sent to the Spring Boot REST API as JSON.

**The current application uses HTTP Basic authentication, not JWT.** After login, the API helper stores a Base64-encoded email/password value in browser `localStorage` under `basicAuth`, and sends it in the `Authorization: Basic ...` header for protected requests.

This is a **local-development implementation**, not a recommended production credential-storage pattern. The separate `src/api/http.js` contains a Bearer-token Axios helper, but that helper does not represent the active authentication flow.

## Current Scope and Future Improvements

- Add a dedicated password-reset interface (the backend has password-reset endpoints, but the current React routes do not include this page).
- Replace Basic credentials stored in `localStorage` with a production-appropriate session/authentication solution.
- Expand validation feedback, loading states, and automated UI tests.
- Introduce configurable production API URLs and deployment settings.

For backend endpoints and database setup, see the [backend README](../backend/README.md). For the overall project overview, see the [root README](../README.md).
