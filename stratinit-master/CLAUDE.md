# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Strategic Initiative is a multiplayer strategy game with a Java server, REST API, React SPA frontend, and legacy Wicket web UI / SWT desktop client (being retired).

## Build Commands

```bash
# Build without tests
mvn clean install -DskipTests

# Build with tests (uses H2 in-memory DB, no external dependencies needed)
mvn clean install

# Run a single test
mvn test -pl stratinit-server -Dtest=WorldManagerTest

# Run a single test class in a specific module
mvn test -pl stratinit-dao -Dtest=PlayerDaoTest

# Run the Spring Boot REST server
mvn spring-boot:run -pl stratinit-rest

# Run the React dev server (hot reload, proxies API to localhost:8081)
cd stratinit-ui && npm run dev

# Database migration (requires PostgreSQL running)
mvn compile flyway:migrate -pl stratinit-dao

# Start PostgreSQL via Docker
docker run -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres
```

## Module Architecture

```
stratinit-graph     → Graph algorithms (no dependencies)
stratinit-core      → Domain entities, DTOs, enums (depends on graph)
stratinit-dao       → JPA repositories, DAOs, caching, Flyway migrations (depends on core)
stratinit-test      → Test helpers and configuration (depends on dao)
stratinit-server    → Game engine, services, business logic (depends on dao)
stratinit-rest      → REST API via Spring Boot (depends on server, ui)
stratinit-ui        → React SPA frontend (Vite, TypeScript, React 18)
stratinit-wicket    → Wicket web UI [legacy, being retired] (depends on server)
```

Dependencies flow downward. `stratinit-client-master/` is a separate module tree for the SWT desktop client (being retired).

## Tech Stack

- **Java 21**, Maven multi-module build
- **Spring Boot 3.5** with Spring Security, Spring Data JPA, WebSocket (STOMP)
- **Jakarta EE** namespace (`jakarta.persistence.*`, `jakarta.annotation.*`)
- **QueryDSL 5.1** with Jakarta classifier (Q-classes generated via APT)
- **React 18** + TypeScript + Vite + Tailwind CSS for SPA frontend
- **JWT authentication** (JJWT 0.12.6) with HTTP Basic fallback
- **PostgreSQL** (production) / **H2** (tests)
- **Flyway 11** for database migrations (`stratinit-dao/src/main/resources/db/migration/`)
- **JUnit 5** + Mockito for testing

## Key Architecture Patterns

**Request processing flow:** Domain controllers (`GameController`, `UnitController`, `CityController`, `NationController`, `MessageController`) handle reads via `RequestProcessor` (calls service methods directly, wraps in `Result<T>`) and writes via `WriteProcessor` (synchronizes on `GameCache`, checks command points, sets lastAction, sends WebSocket notifications). Business logic lives in service classes (`UnitSvc`, `CitySvc`, `NationSvc`, `RelationSvc`); controllers are thin lambdas.

**Authentication:** JWT tokens via `POST /stratinit/auth/login`. `JwtAuthenticationFilter` validates Bearer tokens. HTTP Basic still supported for backward compatibility. Stateless sessions.

**WebSocket:** STOMP over SockJS at `/ws`. `GameNotificationService` pushes updates to `/topic/game/{gameId}` after write operations and scheduled events.

**Data access:** Spring Data JPA repositories (`stratinit-dao/.../repo/`) wrapped by DAO classes (`stratinit-dao/.../dao/`) which also manage `DataCache`. Service layer DAO wrappers (e.g., `UnitDaoService`, `CityDaoService`) live in `stratinit-server`.

**Caching:** `DataCache` holds in-memory game state using `ConcurrentHashMap` (`GameCache`, `NationCache`, `UnitCache`, `CityCache`). Games are loaded via `GameLoaderService`.

**Domain model:** Entities in `stratinit-core/.../client/model/` — key entities are `Game`, `Player`, `Nation`, `Unit`, `City`, `Sector`. Unit type definitions (stats, abilities) are in `unit-definitions.json`, loaded by `UnitBaseLoader` into the `UnitBase` class hierarchy.

**DTOs:** Prefixed with `SI` (e.g., `SIGame`, `SINation`, `SIUnit`) in `stratinit-core`.

**Predicates:** Custom predicate classes in `stratinit-core/.../world/predicate/` and `stratinit-dao/.../dao/predicates/` implement `java.util.function.Predicate` (method: `test()`). Used with Java streams throughout.

**REST paths:** Defined in `SIRestPaths`, base path `/stratinit/`. Server runs on port 8081.

**React frontend:** Dev server on port 5173 with Vite proxy to backend. Built assets bundled into `stratinit-rest` JAR via Maven. Game page at `/game/:gameId` renders HTML5 Canvas map with side panel (7 tabs: Sector, Units, Cities, Battle, Players, Mail, News). State managed via `GameContext` (`useReducer`) with coord-keyed lookup maps. TypeScript types in `src/types/game.ts` mirror Java DTOs. Unit commands (disband, cancel move, build city, switch terrain, cede) in Units tab. Messaging (inbox, sent, announcements, compose) in Mail tab. News log viewer in News tab.

## Testing

Tests use H2 in-memory database (`MODE=LEGACY`) with Flyway migrations applied automatically. Test config in `src/test/resources/persistence.properties` and `application.yml`. The `stratinit-test` module provides helpers: `PlayerHelper`, `GameHelper`, `NationHelper`, `UnitHelper`, `WorldHelper`, `SectorHelper`.

Base test class for REST tests: `BaseStratInitControllerTest` in `stratinit-rest` (extends `StratInitDaoBase`).

## Configuration

- JPA config: `persistence.properties` in each module's resources
- App config: `application.yml` in `stratinit-rest` and `stratinit-wicket`
- Maven JVM settings: `.mvn/jvm.config` (`-Xmx2048m -Xms1024m -Djava.awt.headless=true`)
- Custom properties: `stratinit.email.enabled`, `stratinit.scheduler.enabled`, `stratinit.mode`
- JWT config: `stratinit.jwt.secret`, `stratinit.jwt.expiration-ms`
- CORS: `stratinit.cors.allowed-origins` (defaults to `http://localhost:5173`)
- Unit definitions: `stratinit-core/src/main/resources/unit-definitions.json` (23 unit types with stats and abilities)
