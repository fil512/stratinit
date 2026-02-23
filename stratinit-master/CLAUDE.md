# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Strategic Initiative is a multiplayer strategy game with a Java server, REST API, and React SPA frontend.

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
mvn liquibase:update -pl stratinit-dao

# Start PostgreSQL via Docker
docker run -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres
```

## Module Architecture

```
stratinit-graph     → Graph algorithms (no dependencies)
stratinit-core      → Domain entities, DTOs, enums (depends on graph)
stratinit-dao       → JPA repositories, DAOs, caching, Liquibase migrations (depends on core)
stratinit-test      → Test helpers and configuration (depends on dao)
stratinit-server    → Game engine, services, business logic (depends on dao)
stratinit-rest      → REST API via Spring Boot (depends on server, ui)
stratinit-ui        → React SPA frontend (Vite, TypeScript, React 18)
```

Dependencies flow downward.

## Tech Stack

- **Java 21**, Maven multi-module build
- **Spring Boot 3.5** with Spring Security, Spring Data JPA, WebSocket (STOMP)
- **Jakarta EE** namespace (`jakarta.persistence.*`, `jakarta.annotation.*`)
- **QueryDSL 5.1** with Jakarta classifier (Q-classes generated via APT)
- **React 18** + TypeScript + Vite + Tailwind CSS + Recharts for SPA frontend
- **JWT authentication** (JJWT 0.12.6) with HTTP Basic fallback
- **PostgreSQL** (production) / **H2** (tests)
- **Liquibase 4.32** for database migrations (`stratinit-dao/src/main/resources/db/changelog/`)
- **Bean Validation** (`jakarta.validation`) on request DTOs, `@Valid` in controllers
- **springdoc-openapi 2.8.5** — Swagger UI at `/swagger-ui.html`, OpenAPI spec at `/v3/api-docs`
- **JUnit 5** + Mockito for testing

## Key Architecture Patterns

**Request processing flow:** Domain controllers (`GameController`, `UnitController`, `CityController`, `NationController`, `MessageController`, `RankingController`, `ProfileController`, `AdminController`) return domain DTOs directly (not wrapped in `Result<T>`). Reads go through `RequestProcessor`, writes through `WriteProcessor` (synchronizes on `GameCache`, checks command points, sets lastAction, sends WebSocket notifications, unwraps `Result<T>` from service lambdas — throwing `CommandFailedException` on failure). Error handling is centralized in `GlobalExceptionHandler` (`@RestControllerAdvice`): business errors → HTTP 400, unexpected errors → HTTP 500. Exception hierarchy: `StratInitException` → `CommandFailedException`, `InsufficientCommandPointsException`. Controllers are thin lambdas delegating to REST service classes (`UnitSvc`, `CitySvc`, `NationSvc`, `RelationSvc`) for request-level orchestration. Deeper business logic lives in domain service classes (`UnitService`, `CityService`, `RelationService`, `SectorService`, etc.) in `stratinit-server/.../server/service/`.

**Error handling:** Centralized in `GlobalExceptionHandler` (`@RestControllerAdvice`). Exception hierarchy in `stratinit-core/.../remote/exception/`: `StratInitException` (base) → `CommandFailedException` (wraps failed service `Result` with messages), `InsufficientCommandPointsException`. Bean Validation errors (`MethodArgumentNotValidException`) return HTTP 400 with field-level messages. Business errors return HTTP 400 with `ErrorResponse` JSON (`{error, messages}`). Unexpected errors return HTTP 500.

**Authentication:** JWT tokens via `POST /stratinit/auth/login`. Player registration via `POST /stratinit/auth/register`. Password recovery via `POST /stratinit/auth/forgot-password`. Account settings via `GET/PUT /stratinit/profile` (`ProfileController`). `JwtAuthenticationFilter` validates Bearer tokens. HTTP Basic still supported for backward compatibility. Stateless sessions.

**WebSocket:** STOMP over SockJS at `/ws`. `GameNotificationService` pushes typed JSON messages to `/topic/game/{gameId}`: `{type: "UPDATE", gameId, nationId}` after write operations and scheduled events, `{type: "BATTLE", gameId, x, y}` after combat. `BattleLogService` sends battle notifications on every battle log save. Client (`useGameSocket.ts`) receives typed `GameSocketMessage` and triggers state refresh. Battle logs are populated in every `SIUpdate` via `PlayerWorldViewUpdate` (queries all 4 battle log types through `NationSvc.getBattleLogs()`).

**Data access:** Spring Data JPA repositories (`stratinit-dao/.../repo/`) wrapped by DAO classes (`stratinit-dao/.../dao/`) which also manage `DataCache`. ~30 classes inject DAOs directly for data operations.

**Caching:** `DataCache` holds in-memory game state using `ConcurrentHashMap` (`GameCache`, `NationCache`, `UnitCache`, `CityCache`). Games are loaded via `GameLoaderService`.

**Domain model:** Entities in `stratinit-core/.../client/model/` — key entities are `Game`, `Player`, `Nation`, `Unit`, `City`, `Sector`. Unit type definitions (stats, abilities) are in `unit-definitions.json`, loaded by `UnitBaseLoader` into the `UnitBase` class hierarchy. Value classes `EventKey` and `SectorCoordVector` are Java records. Game balance constants remain in `Constants.java` (used by non-Spring-managed classes).

**DTOs:** Prefixed with `SI` (e.g., `SIGame`, `SINation`, `SIUnit`) in `stratinit-core`.

**Predicates:** Custom predicate classes in `stratinit-core/.../world/predicate/` and `stratinit-dao/.../dao/predicates/` implement `java.util.function.Predicate` (method: `test()`). Used with Java streams throughout.

**REST paths:** Defined in `SIRestPaths`, base path `/stratinit/`. Server runs on port 8081.

**React frontend:** Dev server on port 5173 with Vite proxy to backend. Built assets bundled into `stratinit-rest` JAR via Maven. Routes: `/login`, `/register`, `/forgot-password`, `/games` (joined + available games with join), `/game/:gameId` (gameplay), `/leaderboard`, `/rankings`, `/stats` and `/stats/:gameId` (unit statistics charts), `/settings` (account settings), `/admin` (admin panel, visible to ROLE_ADMIN users only). Game page renders HTML5 Canvas map with side panel (7 tabs: Sector, Units, Cities, Battle, Players, Mail, News). State managed via `GameContext` (`useReducer`) with coord-keyed lookup maps. TypeScript types in `src/types/game.ts` mirror Java DTOs. Unit commands (disband, cancel move, build city, switch terrain, cede) in Units tab. Concede button in Players tab. Messaging (inbox, sent, announcements, compose) in Mail tab. News log viewer in News tab. Unit statistics page with Recharts pie chart (unit love metric) and stacked area charts (units built per day by LAND/NAVY/AIR/TECH category). Account settings page for updating email, password, and email notification preferences.

**Bot players:** Server-side AI players that can be added from the game lobby via `POST /stratinit/add-bot`. Bots call domain services directly (no REST/HTTP/JWT). Games with bots start immediately (skip 24-hour scheduling delay). Bot turns execute periodically via `BotTurnEvent` (every 5 minutes, blitz-scaled). The AI uses a **Utility AI** pattern with configurable weights in `bot-weights.json` (designed for future RL tuning). Key classes: `BotService` (join/create), `BotExecutor` (orchestration), `BotActionGenerator` (candidate generation), `BotWorldState` (snapshot), `BotWeights` (tunable parameters). Six action types: city production, expansion movement, build city, attack enemy, defend city, set diplomacy. Bots are skipped for email notifications and scoring. See [BOTS.md](BOTS.md) for full architecture details.

**Bot RL training:** Evolutionary hill-climbing framework for tuning bot weights. Population of 8 weight configs compete in simulated 4-bot blitz games, scored by cities owned + units + tech. Top 4 survive each generation; bottom 4 are replaced by mutated/crossed winners. Key classes in `server/bot/training/`: `TrainingSession` (orchestration), `TrainingGameSimulator` (synchronous game simulation), `TrainingScorer` (performance scoring), `WeightMutator` (Gaussian mutation + uniform crossover). Results saved to `training-results/best-weights.json` and `training-results/score-history.json`. Run via: `mvn test -pl stratinit-server -Dtest=BotRLTrainingTest`.

## Testing

Tests use H2 in-memory database (`MODE=LEGACY`) with Liquibase migrations applied programmatically via `TestConfig`. Test config in `src/test/resources/persistence.properties` and `application.yml`. The `stratinit-test` module provides helpers: `PlayerHelper`, `GameHelper`, `NationHelper`, `UnitHelper`, `WorldHelper`, `SectorHelper`.

Base test class for REST tests: `BaseStratInitControllerTest` in `stratinit-rest` (extends `StratInitDaoBase`).

## Configuration

- JPA config: `persistence.properties` in each module's resources
- App config: `application.yml` in `stratinit-rest`
- Maven JVM settings: `.mvn/jvm.config` (`-Xmx2048m -Xms1024m -Djava.awt.headless=true`)
- Custom properties: `stratinit.version`, `stratinit.email.enabled`, `stratinit.email.from-address`, `stratinit.email.admin-address`, `stratinit.scheduler.enabled`, `stratinit.mode`
- Email config: `EmailProperties` (`@ConfigurationProperties(prefix = "stratinit.email")`) in `stratinit-server`
- JWT config: `stratinit.jwt.secret`, `stratinit.jwt.expiration-ms`
- CORS: `stratinit.cors.allowed-origins` (defaults to `http://localhost:5173`)
- Unit definitions: `stratinit-core/src/main/resources/unit-definitions.json` (23 unit types with stats and abilities)
- Bot AI weights: `stratinit-server/src/main/resources/bot-weights.json` (loaded by `BotWeightsConfig`, falls back to defaults)
- Training results: `training-results/best-weights.json`, `training-results/score-history.json` (generated by RL training, gitignored)
- Graceful shutdown: `ShutdownHook` (`@PreDestroy`) calls `ServerManager.shutdown()` to flush caches on container stop
