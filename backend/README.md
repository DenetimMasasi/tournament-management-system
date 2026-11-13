# Tournament Management System — Backend

The backend of the [Tournament Management System](../README.md) is a Java **Spring Boot REST API** that handles user accounts, tournament registration, bracket generation, and match result confirmation. Data is persisted in PostgreSQL through Spring Data JPA.

> **Repository history:** The application was developed before its source code was later imported into GitHub. The repository's first commit is not the original development start date.

## Technology Stack

- **Java 21** and **Spring Boot 4.0.1**
- **Spring Web MVC** for REST endpoints
- **Spring Security** with HTTP Basic authentication
- **Spring Data JPA / Hibernate** and **PostgreSQL**
- **Bean Validation** for request validation
- **Spring Mail** for confirmation and password-reset emails
- **Maven Wrapper** for building and running the application

## Responsibilities

- **Accounts:** Registration, login, email verification, and password-reset endpoints.
- **Tournaments:** Create tournaments, edit organizer-controlled fields, browse/search/filter listings, and view details.
- **Applications:** Enforce registration deadlines, tournament capacity, unique participants, license numbers, and rankings.
- **Seeding:** Generate single-elimination brackets from participant rankings and automatically advance participants who receive a bye.
- **Matches:** Record each participant's selected winner. A result is confirmed only when both players agree; conflicting submissions are cleared. Confirmed winners progress to the next round.
- **Personalized data:** Retrieve tournaments and upcoming matches relevant to the authenticated user.

## Project Structure

```text
src/
├── main/
│   ├── java/com/emre/tournament/
│   │   ├── auth/          # Authentication controllers, DTOs, tokens, and services
│   │   ├── config/        # Spring Security, CORS, exception handling
│   │   ├── tournament/    # Tournament/match controllers, entities, DTOs, services, repositories
│   │   ├── user/          # User entity and persistence
│   │   └── TournamentApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/emre/tournament/
```

## Run Locally

### Prerequisites

- Java 21
- A running PostgreSQL server
- An available database, by default named `tournament`

### 1. Configure the database

Create a PostgreSQL database:

```sql
CREATE DATABASE tournament;
```

The application reads connection settings from environment variables:

| Variable | Description | Default |
| --- | --- | --- |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/tournament` |
| `DB_USERNAME` | PostgreSQL user | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | Empty |

Set the values for your local environment before starting the application. Do **not** commit credentials to the repository.

### 2. Optional email configuration

The development configuration uses the Mailtrap SMTP sandbox (`sandbox.smtp.mailtrap.io:587`). For email delivery, supply:

| Variable | Description |
| --- | --- |
| `MAIL_USERNAME` | SMTP username |
| `MAIL_PASSWORD` | SMTP password |

If sending an email fails, the confirmation or reset link is printed in the backend console for local development. The password-reset flow should be reviewed before production use, including the reset link's destination.

### 3. Start Spring Boot

From the `backend/` directory:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The default API base URL is **http://localhost:8080**.

## REST API

### Authentication

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Create an account |
| `GET` | `/api/auth/confirm?token=...` | Verify an email address |
| `POST` | `/api/auth/login` | Verify email/password and return user details |
| `POST` | `/api/auth/forgot-password` | Request a password reset |
| `POST` | `/api/auth/reset-password` | Submit a reset token and new password |

### Tournaments and matches

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/tournaments` | List tournaments; supports `q`, `city`, `sportType`, `status` and pagination |
| `GET` | `/api/tournaments/upcoming` | List upcoming tournaments with search/filter/pagination |
| `GET` | `/api/tournaments/{id}` | View tournament details |
| `POST` | `/api/tournaments` | Create a tournament |
| `PUT` | `/api/tournaments/{id}` | Update a tournament as its organizer |
| `POST` | `/api/tournaments/{id}/apply` | Apply to a tournament |
| `POST` | `/api/tournaments/{id}/seed` | Generate a ranked knockout bracket |
| `GET` | `/api/tournaments/{id}/matches` | List the tournament's matches |
| `POST` | `/api/matches/{id}/result` | Submit a winner selection for a match |
| `GET` | `/api/tournaments/my-upcoming` | Retrieve the authenticated user's upcoming tournaments |
| `GET` | `/api/tournaments/my-upcoming-games` | Retrieve the authenticated user's upcoming matches |

Tournament listing/detail and match-list requests are public. Creation, applications, seeding, updates, match submissions, and personalized endpoints require authentication. Organizer and participant checks are performed in the corresponding services.

### Authentication model

Protected requests use **HTTP Basic authentication**. The login endpoint returns user information; it does **not** issue JWT access tokens.

Example of an authenticated request, substituting your own local credentials:

```bash
curl -u "your-email@example.com:your-password" \
  http://localhost:8080/api/tournaments/my-upcoming
```

## Validation and Match Flow

1. A registered, enabled user applies before the tournament deadline and provides a unique license number and ranking.
2. The organizer generates a bracket from registered players. Empty positions receive byes.
3. Both participants of a match submit their chosen winner independently.
4. If both selections match, the match becomes `COMPLETED` and the winner advances; otherwise selections are reset for resubmission.

## Tests and Build

From `backend/`:

```bash
./mvnw test
./mvnw clean package
```

These are available Maven commands; this documentation does not claim that tests or a production deployment have been verified.

## Development and Security Notes

- `spring.jpa.hibernate.ddl-auto=update` is enabled for local development; use versioned migrations in a production environment.
- CSRF protection is disabled in the current Spring Security configuration, which should be revisited for a production authentication design.
- The application uses Basic authentication rather than JWT; production deployment requires HTTPS and a broader review of authentication, authorization, and secret management.
- SMTP and database credentials should only be provided through local environment variables.

See the [root README](../README.md) for the complete application and the [frontend README](../frontend/README.md) for the user interface.
