# Tournament Management System

A full-stack tournament management project featuring a Java Spring Boot backend and a React frontend.

## Tech stack
- **Backend:** Java 21, Spring Boot 4.0.1, Spring Security, Spring Data JPA, PostgreSQL, Maven
- **Frontend:** React, Vite, React Router, Axios

## Project structure
```
backend/   Spring Boot REST API
frontend/  React web interface
```

## Getting started

### Backend
1. Install Java 21 and PostgreSQL.
2. Create a PostgreSQL database named `tournament`, or set `DB_URL` to your own connection URL.
3. Set the environment variables `DB_USERNAME` and `DB_PASSWORD` to your database credentials.
4. If using email features, set `MAIL_USERNAME` and `MAIL_PASSWORD` to valid SMTP credentials.
5. In `backend/`, run `./mvnw spring-boot:run` (macOS/Linux) or `mvnw.cmd spring-boot:run` (Windows).

### Frontend
1. Install Node.js and npm.
2. In `frontend/`, run `npm install` followed by `npm run dev`.

## Security
The committed `backend/src/main/resources/application.properties` uses environment variables for credentials. Never commit real passwords, API keys, or `.env` files. Keep secrets locally and rotate any credentials that have been shared.
