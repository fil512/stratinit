# Strategic Initiative

A multiplayer strategy game with a Java Spring Boot server, REST API, React SPA frontend, and real-time WebSocket updates.

## Quick Start

```bash
# Start PostgreSQL via Docker
docker run -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres

# Build (skip tests for speed)
cd stratinit-master
mvn clean install -DskipTests

# Database migration
mvn compile flyway:migrate -pl stratinit-dao

# Run the Spring Boot server (port 8081)
mvn spring-boot:run -pl stratinit-rest

# Run the React dev server (port 5173, hot reload, proxies API to backend)
cd stratinit-ui && npm run dev
```

## Access the App

- **Development:** http://localhost:5173 (React dev server, proxies API to backend)
- **Production:** http://localhost:8081 (bundled static assets served by Spring Boot)

## Run Tests

```bash
# All tests (uses H2 in-memory DB, no external dependencies needed)
mvn clean install

# Single test class
mvn test -pl stratinit-server -Dtest=WorldManagerTest
```

## Module Architecture

```
stratinit-graph     → Graph algorithms (no dependencies)
stratinit-core      → Domain entities, DTOs, enums (depends on graph)
stratinit-dao       → JPA repositories, DAOs, caching, Flyway migrations (depends on core)
stratinit-test      → Test helpers and configuration (depends on dao)
stratinit-server    → Game engine, services, request processing (depends on dao)
stratinit-rest      → REST API via Spring Boot (depends on server, ui)
stratinit-ui        → React SPA frontend (Vite, TypeScript, React 18)
stratinit-wicket    → Wicket web UI [legacy, being retired] (depends on server)
```

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, WebSocket (STOMP)
- **Frontend:** React 18, TypeScript, Vite, Tailwind CSS v4
- **Auth:** JWT (JJWT 0.12.6) with HTTP Basic fallback
- **Database:** PostgreSQL (production) / H2 (tests), Flyway migrations
- **Testing:** JUnit 5, Mockito

## React SPA Features

- Login with JWT authentication
- Game list and game selection
- HTML5 Canvas game map with terrain, city, and unit rendering
- Side panel with 7 tabs:
  - **Sector** — terrain info, city production management
  - **Units** — unit list, selection, move, disband, cancel move, build city, switch terrain, cede to allies
  - **Cities** — all cities with build/next build controls
  - **Battle** — battle log with combat details
  - **Players** — nation list, diplomacy/relation management
  - **Mail** — inbox, sent mail, announcements, compose messages
  - **News** — news log organized by day with categorized entries
- Real-time WebSocket updates via STOMP

## Legacy Clients (Being Retired)

```bash
# SWT desktop client
cd stratinit-client-master
mvn -Pwin64 clean install
```
