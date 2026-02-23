# Architectural Modernization Recommendations

Prioritized recommendations for modernizing the Strategic Initiative codebase, organized by impact and effort.

---

## Priority 1: High Impact, Enables Other Work

### 1. Replace Wicket + SWT with a Modern SPA

**Status: DONE — SPA at feature parity, Wicket + SWT retired**

**What's done:**
- `stratinit-ui` Maven module created with `frontend-maven-plugin` (Node 20, Vite, React 18, TypeScript)
- Tailwind CSS v4 with `@tailwindcss/vite` plugin
- Login page (`LoginPage.tsx`) authenticates via JWT and stores token
- Game list page (`GameListPage.tsx`) fetches `GET /stratinit/joinedGames` with Bearer token, links to game pages
- API client (`client.ts`) adds JWT `Authorization` header to all requests
- WebSocket hook (`useGameSocket.ts`) connects to STOMP `/ws` and subscribes to `/topic/game/{gameId}`
- Vite dev proxy routes `/stratinit` and `/ws` to `localhost:8081`
- `WebConfig.java` serves SPA with fallback to `index.html` for client-side routing
- Static assets from `stratinit-ui` are on the classpath via dependency in `stratinit-rest`
- TypeScript types (`src/types/game.ts`) mirroring all Java DTOs
- Game API module (`src/api/game.ts`) with API wrappers for setGame, getUpdate, moveUnits, updateCity, setRelation
- Game state context (`src/context/GameContext.tsx`) using `useReducer` with coord-keyed lookup maps
- HTML5 Canvas map component (`src/components/GameMap.tsx`) — 8px cells, Y-axis inversion, terrain/city/unit rendering, click-to-select/move
- Side panel with 7 tabs: Sector info, Units list, Cities overview, Battle log, Players/diplomacy, Mail, News
- App shell with header, routing (`/game/:gameId`), logout
- WebSocket messages trigger automatic state refresh
- City production management with build/nextBuild dropdowns
- Diplomacy: change relations via dropdown in Players tab
- Unit commands: disband, cancel move, build city, switch terrain (in Units tab)
- Cede units to allied nations (in Units tab)
- Messaging UI: inbox, sent mail, announcements, compose message (Mail tab)
- News log viewer with categorized entries by day (News tab)
- Game browsing and joining: `GameListPage.tsx` shows "My Games" and "Available Games" sections, unjoined games have "Join" button
- Concede from game: Concede button with two-step confirmation in Players tab
- Player registration: `POST /stratinit/auth/register` endpoint in `AuthController.java`, `RegistrationPage.tsx` with username/email/password form, linked from login page
- Leaderboard: `GET /stratinit/leaderboard` endpoint in `RankingController.java`, `LeaderboardPage.tsx` with player rankings sorted by wins
- Team rankings: `GET /stratinit/rankings/team` endpoint in `RankingController.java`, `RankingsPage.tsx` with ELO-based team rankings
- App navigation: `AppShell.tsx` header nav with links to Games, Leaderboard, Rankings
- New DTOs: `SIPlayerRank.java` for player leaderboard data; `SITeamRank` (existing) used for team rankings

- Unit statistics charts replacing Google Charts (shut down 2023):
  - `UnitStatisticsService.java` in `stratinit-server` — aggregation logic extracted from Wicket's `UnitsBuiltProvider`/`BuildAuditsAggregator`
  - New DTOs: `SIUnitLove.java` (unit type + love value), `SIUnitDayRow.java` (day + per-unit-type counts), `SIGameHistory.java` (completed game info)
  - New REST endpoints in `RankingController`: `/stats/games`, `/stats/players`, `/stats/game-units`, `/stats/player-units`
  - `UnitStatsPage.tsx` with Recharts: pie chart for "unit love" metric, 4 stacked area charts (LAND/NAVY/AIR/TECH) for units built per day
  - Game selector dropdown (completed games), player selector dropdown
  - Nav link "Stats" in `AppShell.tsx`, routes `/stats` and `/stats/:gameId` in `App.tsx`
- Account settings page: `ProfileController.java` with `GET /stratinit/profile` and `PUT /stratinit/profile` endpoints (requires auth), `SettingsPage.tsx` with profile info display, email update, email notification preference toggle, password change with confirmation. Settings link in `AppShell.tsx` header, `/settings` route in `App.tsx`
- Password recovery: `POST /stratinit/auth/forgot-password` endpoint in `AuthController.java` (calls existing `PlayerService.forgottenPassword()` which generates random password and emails it), `ForgotPasswordPage.tsx` with username or email lookup, linked from login page
- Admin panel: `AdminController.java` with `GET /stratinit/admin/players`, `POST /stratinit/admin/announcement`, `POST /stratinit/admin/shutdown` endpoints (secured to `ROLE_ADMIN` in `RestWebSecurityAdapterConfig`). `AdminPage.tsx` with post announcement form, player list table, server shutdown button with two-step confirmation. Admin link in `AppShell.tsx` nav (conditionally visible based on `admin` flag from `GET /stratinit/profile`). `ProfileController` response now includes `admin` boolean.
- Builds and passes all 426 tests via `mvn clean install`

**What remains:**
- Responsive/mobile layout

**Key files:**
- `stratinit-ui/src/pages/LoginPage.tsx`, `RegistrationPage.tsx`, `GameListPage.tsx`, `GamePage.tsx`, `LeaderboardPage.tsx`, `RankingsPage.tsx`
- `stratinit-ui/src/api/auth.ts`, `client.ts`, `game.ts`
- `stratinit-ui/src/types/game.ts`
- `stratinit-ui/src/context/GameContext.tsx`
- `stratinit-ui/src/components/GameMap.tsx`, `AppShell.tsx`, `SidePanel.tsx`
- `stratinit-ui/src/components/tabs/SectorTab.tsx`, `UnitsTab.tsx`, `CitiesTab.tsx`, `BattleLogTab.tsx`, `PlayersTab.tsx`, `MessagesTab.tsx`, `NewsTab.tsx`
- `stratinit-ui/src/hooks/useGameSocket.ts`
- `stratinit-rest/.../config/WebConfig.java` (SPA routing)
- `stratinit-rest/.../controller/AuthController.java` (login + registration)
- `stratinit-rest/.../controller/RankingController.java` (leaderboard + team rankings + unit statistics endpoints)
- `stratinit-core/.../dto/SIPlayerRank.java` (player leaderboard DTO)
- `stratinit-core/.../dto/SIUnitLove.java`, `SIUnitDayRow.java`, `SIGameHistory.java` (unit statistics DTOs)
- `stratinit-server/.../service/UnitStatisticsService.java` (aggregation logic for unit statistics)
- `stratinit-ui/src/pages/UnitStatsPage.tsx` (Recharts pie + area charts)
- `stratinit-rest/.../controller/ProfileController.java` (account settings endpoints, admin flag)
- `stratinit-rest/.../controller/AdminController.java` (admin endpoints: players, announcements, shutdown)
- `stratinit-ui/src/pages/SettingsPage.tsx` (account settings UI)
- `stratinit-ui/src/pages/AdminPage.tsx` (admin panel UI)

---

### 2. Add WebSocket for Real-Time Game Updates

**Status: DONE — infrastructure complete, topic design can evolve**

**What's done:**
- `spring-boot-starter-websocket` added to `stratinit-rest`
- `spring-websocket` and `spring-messaging` added to `stratinit-server`
- `WebSocketConfig.java` — STOMP endpoint at `/ws` with SockJS fallback, simple broker on `/topic`, app prefix `/app`
- `WebSocketSecurityConfig.java` — requires authentication for subscriptions to `/topic/game/**`
- `WebSocketAuthInterceptor.java` — authenticates STOMP CONNECT frames via JWT token in Authorization header
- `GameNotificationService.java` — sends JSON messages to `/topic/game/{gameId}` (with `@Autowired(required = false)` so tests without WebSocket context still work)
- `WriteProcessor.java` — calls `GameNotificationService.notifyGameUpdate()` after every successful write operation
- `StratInitUpdater.java` — calls `GameNotificationService.notifyGameUpdate()` after tech updates and unit builds
- All 426 tests pass

**What remains:**
- Add more granular topics (e.g., `/topic/game/{gameId}/battles` for battle logs)
- Push richer payloads (currently sends `{type, gameId, nationId}` — could include the actual `SIUpdate` diff)
- Add battle log notifications from relevant event handlers
- Client-side handling: the `useGameSocket` hook exists but the React pages don't yet react to incoming messages (e.g., auto-refresh game state)

**Key files:**
- `stratinit-rest/.../config/WebSocketConfig.java`
- `stratinit-rest/.../config/WebSocketSecurityConfig.java`
- `stratinit-rest/.../config/WebSocketAuthInterceptor.java`
- `stratinit-server/.../svc/GameNotificationService.java`
- `stratinit-server/.../rest/request/WriteProcessor.java` (integration point)
- `stratinit-server/.../event/svc/StratInitUpdater.java` (integration point)

---

### 3. Modernize Authentication

**Status: DONE — JWT auth added alongside existing HTTP Basic**

**What's done:**
- JJWT 0.12.6 dependencies added (api, impl, jackson)
- `JwtProperties.java` — `@ConfigurationProperties(prefix = "stratinit.jwt")` with `secret` and `expirationMs`
- `JwtTokenService.java` — generates/validates JWT tokens using HS256, configurable expiration
- `JwtAuthenticationFilter.java` — reads `Authorization: Bearer <token>`, validates via `JwtTokenService`, sets `SecurityContextHolder`
- `AuthController.java` — `POST /stratinit/auth/login` accepts `{username, password}`, returns `{token, expiresIn}` or 401
- `RestWebSecurityAdapterConfig.java` updated:
  - JWT filter added before `UsernamePasswordAuthenticationFilter`
  - `SessionCreationPolicy.STATELESS`
  - `/stratinit/auth/**` permitted without authentication
  - `/ws/**` and static assets permitted
  - HTTP Basic still works for backward compatibility
- `application.yml` and test `application.yml` updated with JWT secret and expiration config
- `AuthControllerTest.java` — 4 tests: login returns JWT, bad credentials → 401, Bearer token accesses protected endpoint, no token → 401
- All existing tests unaffected (426 pass)

**What remains:**
- Token refresh endpoint (currently tokens just expire after 24h)
- Generate a cryptographically random 256-bit secret for production (current secret is a placeholder)
- Consider CSRF protection for browser-based cookie auth if needed
- OAuth2 support (Google, GitHub login) if desired
- BCrypt password migration for existing users (the REST layer already uses BCrypt)

**Key files:**
- `stratinit-rest/.../config/JwtProperties.java`
- `stratinit-rest/.../config/JwtTokenService.java`
- `stratinit-rest/.../config/JwtAuthenticationFilter.java`
- `stratinit-rest/.../controller/AuthController.java`
- `stratinit-rest/.../config/RestWebSecurityAdapterConfig.java`
- `stratinit-rest/src/main/resources/application.yml`
- `stratinit-rest/src/test/java/.../controller/AuthControllerTest.java`

---

## Priority 2: Reduce Complexity, Improve Maintainability

### 4. Simplify the Data Access Layer

**Status: DONE — Guava→Streams complete, DAO layer pragmatically cleaned up**

**What's done:**
- All Guava `Collections2.filter()`, `Collections2.transform()`, `Iterables.filter/transform/any/find`, `Lists.transform()`, `Predicates.and()` replaced with Java streams across all server-side modules (stratinit-core, stratinit-dao, stratinit-server, stratinit-rest)
- All `Lists.newArrayList()`, `Maps.newHashMap()`, `Sets.newHashSet()` replaced with standard Java constructors
- All predicate/function classes converted from `com.google.common.base.Predicate/Function` to `java.util.function.Predicate/Function` (`apply()` → `test()`)
- Deleted unused Guava function/predicate classes: `SectorToWorldSectorFunction`, `TopUnitFunction`, `HasAmmoPredicate`, `PortPredicate`, `TeamCityPredicate`, `NationCacheToNationFunction`, `UnitSeenToUnitFunction`, `BulletinToSINewsBulletin`
- Zero remaining Guava functional imports in server-side modules (Guava remains for `Strings`, `Preconditions`, etc.)
- Deleted `GameHistoryDaoService` (pure pass-through to 3 repos) — `GameArchiver` now injects repos directly
- Moved `canEstablishCity`/`isBesideCity` business logic from `CityDao` to `CityService` (was marked TODO)
- Renamed `*DaoService` → `*Service` and package `daoservice` → `service` (these are domain services, not DAO wrappers)
- `LogDaoService` → `BattleLogService` (more descriptive name)

**NOT RECOMMENDED — Spring Cache / DAO-merge:**
- **Spring Cache won't work:** `DataCache` is NOT a traditional read cache — it's mutable in-memory game state. `GameCache` is the `synchronized()` monitor object for concurrency. It uses lazy-flush (modified flags + periodic DB write) and supports atomic multi-key operations within synchronized blocks. Spring Cache (`@Cacheable`/`@CacheEvict`) is designed for immutable read-through caching and cannot replace any of this.
- **DAO/DaoService merge won't work:** DAOs live in `stratinit-dao` (lower module), DaoServices live in `stratinit-server` (higher module). DAOs manage cache + DB persistence. DaoServices contain substantial business logic (diplomacy state machines, unit movement, city capture, tech calculations, fog of war). These are genuinely different responsibilities at different architectural levels. ~30 classes inject DAOs directly — DaoServices were never intended as the sole gateway.

**Key files:**
- `stratinit-dao/.../cache/DataCache.java` (mutable in-memory game state, ConcurrentHashMap)
- `stratinit-dao/.../dao/UnitDao.java` (cache + DB persistence layer)
- `stratinit-server/.../service/UnitService.java` (domain service with business logic, renamed from UnitDaoService)

---

### 5. Replace Result/Request Pattern with Spring Idioms

**Status: DONE — request infrastructure eliminated, Result wrapper removed from REST API**

**What's done:**
- Created `RequestProcessor` service replacing `PlayerRequest` boilerplate for GET endpoints with `process(Function<Nation, T>)`, `processNoGame(Function<Player, T>)`, and `processWithGame(Function<Game, T>)` methods
- Inlined 18 GET request classes directly into controllers — controllers now call domain services (NationSvc, CitySvc, UnitSvc, etc.) via `RequestProcessor`
- Created `WriteProcessor` service replacing `PlayerWriteRequest` boilerplate for write endpoints with `process(Function<Nation, Result<T>>, int commandCost)`, `processForGame(int gameId, Function<PlayerSession, Result<T>>)`, and `processDynamicCost(Function<Nation, CommandResult<T>>, int preCheckCost)` methods
- Moved write business logic into service classes: `UnitSvc` (disbandUnits, cancelMoveOrders, cedeUnits, buildCity, switchTerrain, moveUnits), `CitySvc` (cedeCity), `RelationSvc` (setRelation), `NationSvc` (concede)
- Small write operations inlined as controller lambdas: sendMessage, getMail, getBattleLog, setGame, joinGame
- Deleted 17 write request class files + `PlayerWriteRequest` + `PlayerRequest` + `RequestFactory` + `BuildRequest`
- `DataWriter` + `SynchronizedDataAccess` deleted — sync logic inlined directly into `EventUpdate.update()`
- **Controllers return `T` directly** instead of `Result<T>` — the `Result` wrapper is no longer part of the REST API
- **`@RestControllerAdvice` (`GlobalExceptionHandler`)** handles all exceptions centrally: `CommandFailedException` → HTTP 400, `StratInitException` → HTTP 400, unexpected exceptions → HTTP 500 with error email
- **Exception hierarchy:** `StratInitException` (base), `CommandFailedException` (wraps failed `Result` with messages list), `InsufficientCommandPointsException` (command point validation)
- **`WriteProcessor` unwraps `Result<T>`** from service lambdas: on failure throws `CommandFailedException`, on success returns `result.getValue()`
- **React frontend updated:** removed `Result<T>` interface from TypeScript types, all API calls return `T` directly, error handling uses HTTP status codes
- **`Result.java` cleaned up:** removed `commandPoints` and `runMode` fields (no longer serialized to clients). `Result<T>` is still used internally by ~105 service-layer sites.
- All 426 tests passing

**What remains:**
- Deliver battle logs and game events via WebSocket instead of piggybacking on REST responses

**Key files:**
- `stratinit-server/.../rest/request/RequestProcessor.java` (replaces PlayerRequest for reads)
- `stratinit-server/.../rest/request/WriteProcessor.java` (replaces PlayerWriteRequest for writes, unwraps Result)
- `stratinit-rest/.../controller/GlobalExceptionHandler.java` (@ControllerAdvice for centralized error handling)
- `stratinit-rest/.../controller/ErrorResponse.java` (error response record)
- `stratinit-core/.../remote/exception/StratInitException.java`, `CommandFailedException.java`, `InsufficientCommandPointsException.java` (exception hierarchy)
- `stratinit-core/.../remote/Result.java` (service-layer internal use only)
- `stratinit-rest/.../controller/GameController.java`, `UnitController.java`, `CityController.java`, `NationController.java`, `MessageController.java`

---

### 6. Break Up God Classes

**Status: DONE**

**What's done:**
- `StratInitController` (279 lines, 36 endpoints) split into 5 domain controllers:
  - `GameController` — version, serverConfig, unitBases, setGame, joinGame, joinedGames, unjoinedGames, update, concede, submitError
  - `UnitController` — moveUnits, getUnits, getSeenUnits, disbandUnits, cancelMove, buildCity, switchTerrain, cedeUnits, unitsBuilt
  - `CityController` — updateCity, getCities, getSeenCities, cedeCity
  - `NationController` — getNations, getMyNation, getSectors, getRelations, setRelation, getBattleLog, getTeams
  - `MessageController` — sendMessage, getMail, getSentMail, getMessages, getAnnouncements, getNewsLogs
  - `RankingController` — leaderboard, team rankings
- `EventSchedulerImpl` reduced from 14 to 8 autowired dependencies by extracting `GameStartupService` (handles game startup orchestration: city/unit/relation catch-up, fog of war survey, integrity checks)
- `UnitBase` definitions extracted from 185-line static initializer to `unit-definitions.json` config file, loaded at startup by `UnitBaseLoader`
- All 426 tests passing

**Key files:**
- `stratinit-rest/.../controller/GameController.java`, `UnitController.java`, `CityController.java`, `NationController.java`, `MessageController.java`
- `stratinit-server/.../event/svc/GameStartupService.java` (extracted from EventSchedulerImpl)
- `stratinit-core/src/main/resources/unit-definitions.json` (unit definitions)
- `stratinit-core/.../client/model/UnitBaseLoader.java` (JSON loader)

---

### 7. Modernize Concurrency

**Status: PARTIALLY DONE — ConcurrentHashMap and resource leak fixes complete**

**What's done:**
- `DataCache`: replaced `TreeMap` with `ConcurrentHashMap`, removed `synchronized` from `getGameCache()` (uses `putIfAbsent()` pattern) and `flush()`
- `ServerLocker`: fixed `RandomAccessFile` resource leak by storing as instance field and closing in `releaseLock()`
- `Profiler`: replaced `Collections.synchronizedMap(new HashMap<>())` with `ConcurrentHashMap`

**NOT RECOMMENDED — remaining items are not feasible given the architecture:**
- **`@Version` optimistic locking won't work:** Entities live in `DataCache` (ConcurrentHashMap) indefinitely and are flushed to the DB via explicit `repo.save()` every 15 minutes. They are not managed within JPA sessions. Adding `@Version` would cause `OptimisticLockException` on every flush since the version in memory would be stale.
- **`@Transactional` can't replace `synchronized`:** The `synchronized(gameCache)` blocks protect mutable in-memory HashMap/TreeMap state (unit positions, city ownership, nation stats), not database transactions. `@Transactional` boundaries are orthogonal to this concern.
- **`ServerLocker` DB replacement is low value:** The application is deployed as a single server instance. Replacing FileLock with a DB-based lock adds complexity with no practical benefit.

**Key files:**
- `stratinit-dao/.../cache/DataCache.java` (now uses ConcurrentHashMap)
- `stratinit-server/.../event/svc/ServerLocker.java` (resource leak fixed, still uses FileLock)
- `stratinit-server/.../event/update/EventUpdate.java` (`synchronized(gameCache)` — inlined from deleted `SynchronizedDataAccess`)

---

## Priority 3: Dependency and API Hygiene

### 8. Replace Deprecated Dependencies

**Status: DONE — all deprecated dependencies removed**

| Dependency | Version | Status | Replacement |
|---|---|---|---|
| Commons HTTPClient | 3.1 | ~~EOL since 2011~~ **REMOVED** | Was only used by SWT client (now deleted) |
| Jasypt | 1.9.3 | ~~Unmaintained since 2018~~ **REMOVED** | Was only used by SWT client (now deleted) |
| Google Visualization API | jsapi | ~~Shut down 2023~~ **REPLACED** | Recharts in SPA |
| SWT | 3.5.2 | ~~2009 vintage~~ **REMOVED** | `stratinit-client-master` deleted |

**What remains:**
- Nothing — all deprecated dependencies removed

---

### 9. Improve API Design

**Status: DONE — OpenAPI docs + Bean Validation complete**

**What's done:**
- `springdoc-openapi-starter-webmvc-ui` 2.8.5 now active — Swagger UI at `/swagger-ui.html`, OpenAPI spec at `/v3/api-docs`
- `spring-boot-starter-validation` added to `stratinit-rest`, `jakarta.validation-api` + `swagger-annotations` added to `stratinit-core`
- `@Tag` on all 9 controllers: Game Management, Units, Cities, Nations & Diplomacy, Messaging, Rankings & Statistics, Profile, Administration, Authentication
- `@Operation(summary=...)` on every endpoint (40+ endpoints)
- `@Schema(description=...)` on 8 key response DTOs: `SIGame`, `SINation`, `SIUnit`, `SIUpdate`, `SISector`, `SIRelation`, `SIBattleLog`, `SIMessage`
- Bean Validation annotations on request DTOs: `@NotEmpty`, `@NotNull`, `@Positive`, `@NotBlank`, `@Email`, `@Size` as appropriate
- `@Valid` on all `@RequestBody` controller parameters
- `MethodArgumentNotValidException` handler in `GlobalExceptionHandler` → HTTP 400 with field-level `ErrorResponse`
- `AuthController` and `AdminController` error responses standardized to throw `StratInitException` (caught by `GlobalExceptionHandler`) instead of manual `Map.of("error", ...)` responses
- All 426 tests passing

**What remains:**
- API versioning (`/api/v1/...`) — deferred, would require frontend URL updates and adds complexity with no current need

**Key files:**
- `stratinit-rest/.../controller/*.java` — all 9 controllers with `@Tag`, `@Operation`, `@Valid`
- `stratinit-rest/.../controller/GlobalExceptionHandler.java` — validation error handler
- `stratinit-core/.../remote/request/*.java` — Bean Validation annotations
- `stratinit-core/.../dto/SI*.java` — `@Schema` annotations

---

### 10. Domain Model Cleanup

**Status: NOT STARTED**

**Current state:**
- Composite keys using `EventKey` with manual `hashCode`/`equals` (`stratinit-core/.../model/EventKey.java`)
- Manual DTO conversion scattered across 10+ service classes (e.g., `new SIUnit(unit)`, `new SIGame(game)` in `NationSvc.java`, `CitySvc.java`, `UnitSvc.java`)
- 70+ hardcoded constants in `Constants.java` including game balance values, email addresses, and server version
- Unit stats now in `unit-definitions.json` (see #6) but game balance constants still hardcoded

**Recommendation:**
- Replace manual DTO mapping with MapStruct interfaces — define `@Mapper` interfaces for each entity/DTO pair
- Move game balance constants (`TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES`, `HOURS_BETWEEN_UNIT_UPDATES`, etc.) to `application.yml` with `@ConfigurationProperties`
- Replace `EventKey` manual hashCode/equals with Java 16+ `record` classes
- Move email addresses and server version to configuration

**Key files:**
- `stratinit-core/.../type/Constants.java` (70+ hardcoded values)
- `stratinit-core/.../model/EventKey.java` (manual hashCode/equals)
- `stratinit-server/.../svc/NationSvc.java`, `CitySvc.java`, `UnitSvc.java` (manual DTO conversion)

---

## Suggested Migration Order

```
Phase 1: Foundation (no user-facing changes)
  ├── #8  Replace deprecated dependencies            ✅ DONE
  ├── #4  Guava → Java streams (mechanical refactor) ✅ DONE
  └── #7  ConcurrentHashMap in DataCache             ✅ DONE

Phase 2: Backend modernization
  ├── #3  JWT authentication                         ✅ DONE
  ├── #6  Split god classes                          ✅ DONE
  ├── #5  Replace Result/Request pattern             ✅ DONE
  └── #4  Simplify DAO layer (pragmatic cleanup) ✅ DONE

Phase 3: API and frontend
  ├── #9  API versioning + OpenAPI docs              ✅ DONE
  ├── #2  WebSocket support                          ✅ DONE
  └── #1  SPA frontend                               ✅ DONE (feature parity reached, Wicket + SWT retired)

Phase 4: Polish
  └── #10 Domain model cleanup (MapStruct, records, config externalization)
```

Each phase builds on the previous. Phase 1 items are low-risk, mechanical changes that reduce tech debt without changing behavior. Phase 2 modernizes the backend architecture. Phase 3 delivers the user-facing improvements. Phase 4 is cleanup that can happen incrementally.

