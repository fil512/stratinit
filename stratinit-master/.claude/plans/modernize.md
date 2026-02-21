# Architectural Modernization Recommendations

Prioritized recommendations for modernizing the Strategic Initiative codebase, organized by impact and effort.

---

## Priority 1: High Impact, Enables Other Work

### 1. Replace Wicket + SWT with a Modern SPA

**Status: IN PROGRESS — core gameplay screen implemented**

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
- Builds and passes all 426 tests via `mvn clean install`

**What remains:**
- Replace Google Charts with Chart.js or Recharts for unit statistics
- Responsive/mobile layout
- Once SPA reaches feature parity, retire `stratinit-wicket` and `stratinit-client-master` modules

**Key files:**
- `stratinit-ui/src/pages/LoginPage.tsx`, `GameListPage.tsx`, `GamePage.tsx`
- `stratinit-ui/src/api/auth.ts`, `client.ts`, `game.ts`
- `stratinit-ui/src/types/game.ts`
- `stratinit-ui/src/context/GameContext.tsx`
- `stratinit-ui/src/components/GameMap.tsx`, `AppShell.tsx`, `SidePanel.tsx`
- `stratinit-ui/src/components/tabs/SectorTab.tsx`, `UnitsTab.tsx`, `CitiesTab.tsx`, `BattleLogTab.tsx`, `PlayersTab.tsx`, `MessagesTab.tsx`, `NewsTab.tsx`
- `stratinit-ui/src/hooks/useGameSocket.ts`
- `stratinit-rest/.../config/WebConfig.java` (SPA routing)

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
- BCrypt password migration for existing users (the REST layer already uses BCrypt; verify Wicket users are migrated)

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

**Status: PARTIALLY DONE — Guava→Streams complete, DAO tier consolidation not started**

**What's done:**
- All Guava `Collections2.filter()`, `Collections2.transform()`, `Iterables.filter/transform/any/find`, `Lists.transform()`, `Predicates.and()` replaced with Java streams across all server-side modules (stratinit-core, stratinit-dao, stratinit-server, stratinit-rest)
- All `Lists.newArrayList()`, `Maps.newHashMap()`, `Sets.newHashSet()` replaced with standard Java constructors
- All predicate/function classes converted from `com.google.common.base.Predicate/Function` to `java.util.function.Predicate/Function` (`apply()` → `test()`)
- Deleted unused Guava function/predicate classes: `SectorToWorldSectorFunction`, `TopUnitFunction`, `HasAmmoPredicate`, `PortPredicate`, `TeamCityPredicate`, `NationCacheToNationFunction`, `UnitSeenToUnitFunction`, `BulletinToSINewsBulletin`
- Zero remaining Guava functional imports in server-side modules (Guava remains for `Strings`, `Preconditions`, etc.)

**What remains:**
- Replace `DataCache` manual caching with Spring Cache abstraction (`@Cacheable`, `@CacheEvict`) backed by Caffeine
- Merge DAO and DaoService tiers — use repositories directly with `@Cacheable` annotations

**Key files:**
- `stratinit-dao/.../cache/DataCache.java` (now uses ConcurrentHashMap and streams)
- `stratinit-dao/.../dao/UnitDao.java` (now uses streams; wraps `UnitRepo`)
- `stratinit-server/.../daoservice/UnitDaoService.java` (wraps `UnitDao`)

---

### 5. Replace Result/Request Pattern with Spring Idioms

**Status: DONE — all request infrastructure eliminated**

**What's done:**
- Created `RequestProcessor` service replacing `PlayerRequest` boilerplate for GET endpoints with `process(Function<Nation, T>)`, `processNoGame(Function<Player, T>)`, and `processWithGame(Function<Game, T>)` methods
- Inlined 18 GET request classes directly into controllers — controllers now call domain services (NationSvc, CitySvc, UnitSvc, etc.) via `RequestProcessor`
- Created `WriteProcessor` service replacing `PlayerWriteRequest` boilerplate for write endpoints with `process(Function<Nation, Result<T>>, int commandCost)`, `processForGame(int gameId, Function<PlayerSession, Result<T>>)`, and `processDynamicCost(Function<Nation, CommandResult<T>>, int preCheckCost)` methods
- Moved write business logic into service classes: `UnitSvc` (disbandUnits, cancelMoveOrders, cedeUnits, buildCity, switchTerrain, moveUnits), `CitySvc` (cedeCity), `RelationSvc` (setRelation), `NationSvc` (concede)
- Small write operations inlined as controller lambdas: sendMessage, getMail, getBattleLog, setGame, joinGame
- Deleted 17 write request class files + `PlayerWriteRequest` + `PlayerRequest` + `RequestFactory` + `BuildRequest`
- `DataWriter` + `SynchronizedDataAccess` deleted — sync logic inlined directly into `EventUpdate.update()`
- All 426 tests passing

**What remains:**
- Return domain DTOs directly from controller methods instead of `Result<T>` (requires coordinated frontend changes)
- Move error handling to `@ControllerAdvice` with `@ExceptionHandler` methods
- Deliver battle logs and game events via WebSocket instead of piggybacking on REST responses

**Key files:**
- `stratinit-server/.../rest/request/RequestProcessor.java` (replaces PlayerRequest for reads)
- `stratinit-server/.../rest/request/WriteProcessor.java` (replaces PlayerWriteRequest for writes)
- `stratinit-core/.../remote/Result.java` (618-line god object — still used)
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

**What remains:**
- Add JPA optimistic locking (`@Version` column) to `Game`, `Unit`, `City` entities to detect concurrent modifications
- Replace `ServerLocker` file lock with database-based leader election (Spring Integration's `LockRegistry` or a simple `SELECT FOR UPDATE`)
- Use `@Transactional` boundaries instead of manual `synchronized` blocks where possible

**Key files:**
- `stratinit-dao/.../cache/DataCache.java` (now uses ConcurrentHashMap)
- `stratinit-server/.../event/svc/ServerLocker.java` (resource leak fixed, still uses FileLock)
- `stratinit-server/.../event/update/EventUpdate.java` (`synchronized(gameCache)` — inlined from deleted `SynchronizedDataAccess`)

---

## Priority 3: Dependency and API Hygiene

### 8. Replace Deprecated Dependencies

**Status: DONE — commons-httpclient and jasypt removed**

| Dependency | Version | Status | Replacement |
|---|---|---|---|
| Commons HTTPClient | 3.1 | ~~EOL since 2011~~ **REMOVED** | Replaced with `org.apache.http.HttpStatus` + `EnglishReasonPhraseCatalog` (already-present HttpComponents 4.x) |
| Jasypt | 1.9.3 | ~~Unmaintained since 2018~~ **REMOVED** | Replaced with `javax.crypto` AES + PBKDF2 (standard JDK) |
| Google Visualization API | jsapi | Shut down 2023 | Chart.js, Recharts, or Apache ECharts (retires with Wicket) |
| SWT | 3.5.2 | ~2009 vintage | Retire with SPA migration (recommendation #1) |

**What's done:**
- `commons-httpclient` removed from parent `pom.xml` and `stratinit-client/pom.xml`; `RestClient.java` and `ManualRestIntegrationTest.java` migrated to `org.apache.http.HttpStatus` + `EnglishReasonPhraseCatalog`
- `jasypt` removed from parent `pom.xml` and `stratinit-client/pom.xml`; `AccountPersister.java` migrated from `BasicTextEncryptor` to `javax.crypto` AES with PBKDF2 key derivation

**What remains:**
- Google Charts and SWT retire naturally when `stratinit-wicket` and `stratinit-client-master` are removed (#1)

**Key files:**
- `stratinit-client-master/.../rest/RestClient.java` (migrated HttpStatus usage)
- `stratinit-client-master/.../util/AccountPersister.java` (migrated to javax.crypto)

---

### 9. Improve API Design

**Status: NOT STARTED**

**Current state:** 36 endpoints split across 5 domain controllers (see #6), no OpenAPI/Swagger annotations, no API versioning, and a flat namespace under `/stratinit/`. Endpoint paths are defined in `SIRestPaths` as string constants.

**Recommendation:**
- Add `springdoc-openapi-starter-webmvc-ui` for automatic OpenAPI 3.0 docs (already in POM)
- Version the API: `/api/v1/games`, `/api/v1/units`, etc.
- Add `@Operation`, `@ApiResponse`, `@Schema` annotations to document request/response contracts
- Add request validation with `@Valid` and Bean Validation annotations on DTOs

**Key files:**
- `stratinit-rest/.../controller/GameController.java`, `UnitController.java`, `CityController.java`, `NationController.java`, `MessageController.java`
- `stratinit-core/.../remote/SIRestPaths.java` (path constants)

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
  └── #4  Simplify DAO tiers + Spring Cache

Phase 3: API and frontend
  ├── #9  API versioning + OpenAPI docs
  ├── #2  WebSocket support                          ✅ DONE
  └── #1  SPA frontend                               🔧 IN PROGRESS (near feature parity — unit cmds, messaging, news done)

Phase 4: Polish
  ├── #10 Domain model cleanup (MapStruct, records, config externalization)
  └── #1  Retire Wicket + SWT modules
```

Each phase builds on the previous. Phase 1 items are low-risk, mechanical changes that reduce tech debt without changing behavior. Phase 2 modernizes the backend architecture. Phase 3 delivers the user-facing improvements. Phase 4 is cleanup that can happen incrementally.

---

## Files Changed in This Implementation

| File | Action | Part |
|------|--------|------|
| `pom.xml` (parent) | Edit: added jjwt version, stratinit-ui module | 1, 3 |
| `stratinit-rest/pom.xml` | Edit: added jjwt, websocket, security-messaging, stratinit-ui deps | 1, 2, 3 |
| `stratinit-server/pom.xml` | Edit: added spring-websocket, spring-messaging | 2 |
| `stratinit-rest/.../config/JwtProperties.java` | New | 1 |
| `stratinit-rest/.../config/JwtTokenService.java` | New | 1 |
| `stratinit-rest/.../config/JwtAuthenticationFilter.java` | New | 1 |
| `stratinit-rest/.../controller/AuthController.java` | New | 1 |
| `stratinit-rest/.../config/RestWebSecurityAdapterConfig.java` | Edit: JWT filter, bean definitions, stateless sessions, permit paths | 1, 2, 3 |
| `stratinit-rest/src/main/resources/application.yml` | Edit: added jwt + cors config | 1, 3 |
| `stratinit-rest/src/test/resources/application.yml` | Edit: added jwt config for tests | 1 |
| `stratinit-rest/.../config/WebSocketConfig.java` | New | 2 |
| `stratinit-rest/.../config/WebSocketSecurityConfig.java` | New | 2 |
| `stratinit-rest/.../config/WebSocketAuthInterceptor.java` | New | 2 |
| `stratinit-rest/.../config/WebConfig.java` | New | 3 |
| `stratinit-server/.../svc/GameNotificationService.java` | New | 2 |
| `stratinit-server/.../rest/request/WriteProcessor.java` | New: replaces PlayerWriteRequest for all writes | 2, 5 |
| `stratinit-server/.../event/svc/StratInitUpdater.java` | Edit: added notification after events | 2 |
| `stratinit-rest/.../controller/AuthControllerTest.java` | New | 1 |
| `stratinit-ui/pom.xml` | New | 3 |
| `stratinit-ui/package.json` | New | 3 |
| `stratinit-ui/tsconfig.json` | New | 3 |
| `stratinit-ui/vite.config.ts` | New | 3 |
| `stratinit-ui/index.html` | New | 3 |
| `stratinit-ui/src/main.tsx` | New | 3 |
| `stratinit-ui/src/App.tsx` | New | 3 |
| `stratinit-ui/src/api/auth.ts` | New | 3 |
| `stratinit-ui/src/api/client.ts` | New | 3 |
| `stratinit-ui/src/pages/LoginPage.tsx` | New | 3 |
| `stratinit-ui/src/pages/GameListPage.tsx` | New | 3 |
| `stratinit-ui/src/hooks/useGameSocket.ts` | New | 3 |
| `stratinit-ui/src/vite-env.d.ts` | New | 3 |
| `stratinit-server/.../event/update/EventUpdate.java` | Edit: inlined sync logic, removed DataWriter/SynchronizedDataAccess | 5 |
| `stratinit-server/.../rest/svc/DataWriter.java` | Deleted | 5 |
| `stratinit-server/.../rest/svc/SynchronizedDataAccess.java` | Deleted | 5 |
| `stratinit-client-master/.../rest/RestClient.java` | Edit: migrated commons-httpclient to HttpComponents 4.x | 8 |
| `stratinit-client-master/.../util/AccountPersister.java` | Edit: migrated Jasypt to javax.crypto AES | 8 |
| `stratinit-client-master/stratinit-client/pom.xml` | Edit: removed commons-httpclient + jasypt deps | 8 |
| `pom.xml` (parent) | Edit: removed commons-httpclient + jasypt; added javax.annotation-api + httpcomponents version mgmt | 5, 8 |
