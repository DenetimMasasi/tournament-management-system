# Tournament Management System

A full-stack web application for organizing tournaments, managing participant registrations, generating knockout brackets, and recording match results. The project combines a **Spring Boot REST API** with a **React** interface and a **PostgreSQL** database.

> **Project history:** This project was originally developed before it was published on GitHub. The source code was imported into this repository later for documentation and portfolio purposes, so the initial GitHub commit does not represent the start of development.

## Features

- **Tournament discovery:** Browse upcoming tournaments, search by name, and navigate paginated results. The API also supports filtering by city, sport type, and status.
- **Tournament management:** Authenticated users can create tournaments with dates, location, sport type, capacity, and registration deadlines. Organizers can update tournament descriptions and sponsor information.
- **Participant registration:** Users can apply with a license number and ranking. The backend validates deadlines, capacity, duplicate applications, license numbers, and rankings.
- **Knockout bracket generation:** Organizers can seed a single-elimination bracket based on participant rankings, including automatic handling of byes.
- **Match results and progression:** Both players submit their selected winner. A matching result confirms the outcome and advances the winner to the next round; conflicting submissions are reset.
- **Account management:** Registration, email confirmation, and login, with backend endpoints for password recovery.
- **Personalized views:** Users can view upcoming tournaments they joined and retrieve their upcoming matches.

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot 4.0.1, Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation |
| Database | PostgreSQL, Hibernate/JPA |
| Frontend | React 19, JavaScript, React Router, Vite 7, CSS |
| API communication | REST/JSON, Fetch API, Vite development proxy |
| Build tools | Maven Wrapper, npm |
| Email | Spring Mail, Mailtrap SMTP sandbox configuration |

## Project Structure

```text
tournament-management-system/
├── backend/
│   ├── src/main/java/com/emre/tournament/
│   │   ├── auth/          # Registration, login, email confirmation, password recovery
│   │   ├── config/        # Security, CORS, and exception handling
│   │   ├── tournament/    # Tournaments, participants, matches, bracket logic
│   │   └── user/          # User entity and repository
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/    # Bracket visualization
│   │   ├── pages/         # Tournament, authentication, and user pages
│   │   └── services/      # API requests
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## Getting Started

### Prerequisites

- **Java 21**
- **PostgreSQL**
- **Node.js and npm** compatible with Vite 7
- Git (for cloning the repository)

### 1. Clone the repository

```bash
git clone https://github.com/DenetimMasasi/tournament-management-system.git
cd tournament-management-system
```

### 2. Configure PostgreSQL and the backend

Create a PostgreSQL database named `tournament` (or use another name and customize `DB_URL`).

The backend reads these environment variables:

| Variable | Purpose | Default / note |
| --- | --- | --- |
| `DB_URL` | PostgreSQL JDBC connection | `jdbc:postgresql://localhost:5432/tournament` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | Empty by default; set locally |
| `MAIL_USERNAME` | SMTP username | Required for sending emails via configured Mailtrap sandbox |
| `MAIL_PASSWORD` | SMTP password | Required for sending emails via configured Mailtrap sandbox |

The SMTP host is currently set to `sandbox.smtp.mailtrap.io` in `backend/src/main/resources/application.properties`. When email delivery is unavailable, the backend logs confirmation/reset links to its console for local development.

Start the API:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell, use `./mvnw.cmd spring-boot:run` instead. The API runs on **http://localhost:8080** by default.

### 3. Start the frontend

From a separate terminal in the repository root:

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** in your browser. The Vite development server proxies `/api` requests to the backend at port 8080.

### 4. Try the application

1. Register an account and confirm the email using the received link or the link printed in the backend console.
2. Log in and explore upcoming tournaments.
3. Create a tournament or apply to an existing one.
4. As the tournament organizer, generate the bracket after participants have joined.
5. As match participants, submit results to confirm winners and progress through the bracket.

## API Overview

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register a new user |
| `GET` | `/api/auth/confirm?token=...` | Confirm an email address |
| `POST` | `/api/auth/login` | Verify login credentials |
| `POST` | `/api/auth/forgot-password` | Request a password-reset email |
| `POST` | `/api/auth/reset-password` | Reset a password with a token |
| `GET` | `/api/tournaments/upcoming` | Search upcoming tournaments |
| `GET` | `/api/tournaments` | List/filter tournaments |
| `GET` | `/api/tournaments/{id}` | Retrieve tournament details |
| `POST` | `/api/tournaments` | Create a tournament (authenticated) |
| `PUT` | `/api/tournaments/{id}` | Update organizer-editable fields |
| `POST` | `/api/tournaments/{id}/apply` | Apply to a tournament |
| `POST` | `/api/tournaments/{id}/seed` | Generate tournament bracket |
| `GET` | `/api/tournaments/{id}/matches` | List tournament matches |
| `POST` | `/api/matches/{id}/result` | Submit a match result |
| `GET` | `/api/tournaments/my-upcoming` | View joined upcoming tournaments |
| `GET` | `/api/tournaments/my-upcoming-games` | View upcoming matches |

Authentication is handled with **HTTP Basic authentication** on protected endpoints, not JWT. Public tournament listings can be accessed without signing in.

## Development Notes

- The application is configured primarily for **local development and demonstration**; it has not been presented as a production deployment.
- The frontend currently stores Basic authentication credentials in browser local storage. A production version should replace this with a more secure authentication/session design and review CSRF, HTTPS, and other deployment security requirements.
- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update` for local schema updates. Database migrations would be preferable for production.
- The password-recovery endpoints exist in the backend, but there is no dedicated password-reset page in the current React interface.
- Avoid committing database credentials, SMTP passwords, or other secrets. Configure them with environment variables instead.

## Local Checks

Backend (inside `backend/`):

```bash
./mvnw test
```

Frontend (inside `frontend/`):

```bash
npm run lint
npm run build
```

These commands are available in the project; passing tests or a production deployment is not implied by this README.
