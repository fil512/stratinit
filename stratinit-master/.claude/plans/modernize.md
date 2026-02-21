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
- `PlayerWriteRequest.java` — calls `GameNotificationService.notifyGameUpdate()` after every successful write operation
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
- `stratinit-server/.../request/write/PlayerWriteRequest.java` (integration point)
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

**Status: NOT STARTED**

**Current state:** `Result<T>` (`stratinit-core/.../remote/Result.java`, 618 lines) is a god object carrying `success`, `value`, `messages`, `silogs` (battle logs), `moveSuccess`, `commandPoints`, and `runMode`. All 36 REST endpoints return `Result<T>`. 37 `Request` subclasses are instantiated via `RequestFactory` using Spring `@Lookup` methods for prototype-scoped beans.

**Recommendation:**
- Return domain DTOs directly from controller methods; use `ResponseEntity<T>` for status codes
- Move error handling to `@ControllerAdvice` with `@ExceptionHandler` methods
- Deliver battle logs and game events via WebSocket (now available) instead of piggybacking on REST responses
- Replace `Request` classes with service method calls — the `execute()` logic moves to service methods
- Remove `RequestFactory` entirely

**Key files:**
- `stratinit-core/.../remote/Result.java` (618-line god object)
- `stratinit-server/.../rest/request/RequestFactory.java` (37 `@Lookup` methods)
- `stratinit-rest/.../controller/StratInitController.java` (36 endpoints, all returning `Result<T>`)

---

### 6. Break Up God Classes

**Status: NOT STARTED**

**Current state:**
- `EventSchedulerImpl` (`stratinit-server/.../event/svc/EventSchedulerImpl.java`, 258 lines) has **16 `@Autowired` dependencies** and a `// FIXME too many collaborators` comment at line 66
- `UnitBase` (`stratinit-core/.../model/UnitBase.java`, 618 lines) contains all unit type definitions in a static initializer block, with a TODO to split it
- `StratInitController` (`stratinit-rest/.../controller/StratInitController.java`, 279 lines) handles **36 endpoints** spanning games, units, cities, messages, relations, and admin

**Recommendation:**
- Split `EventSchedulerImpl` by event domain: `UnitEventScheduler`, `CityEventScheduler`, `GameEventScheduler`
- Extract `UnitBase` definitions into a YAML/JSON config file or database table, loaded at startup. This enables balancing without recompilation
- Split `StratInitController` into domain-specific controllers: `GameController`, `UnitController`, `CityController`, `MessageController`, `RelationController`, `AdminController`

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
- `stratinit-server/.../rest/svc/SynchronizedDataAccess.java` (`synchronized(gameCache)` — unchanged)

---

## Priority 3: Dependency and API Hygiene

### 8. Replace Deprecated Dependencies

**Status: NOT STARTED**

| Dependency | Version | Status | Replacement |
|---|---|---|---|
| Commons HTTPClient | 3.1 | EOL since 2011 | Apache HttpClient 5.x or `java.net.http.HttpClient` (Java 11+) |
| Jasypt | 1.9.3 | Unmaintained since 2018 | Spring Security Crypto (`Encryptors.text()`) or Tink |
| Google Visualization API | jsapi | Shut down 2023 | Chart.js, Recharts, or Apache ECharts |
| SWT | 3.5.2 | ~2009 vintage | Retire with SPA migration (recommendation #1) |

**Key files:**
- `pom.xml:159-162` (commons-httpclient 3.1)
- `pom.xml:193-197` (jasypt 1.9.3)
- `stratinit-client-master/.../util/AccountPersister.java` (Jasypt `BasicTextEncryptor` usage)
- `stratinit-wicket/.../unit/playerUnitsChart.js` (Google Charts)

---

### 9. Improve API Design

**Status: NOT STARTED**

**Current state:** Single monolithic `StratInitController` with 36 endpoints, no OpenAPI/Swagger annotations, no API versioning, and a flat namespace under `/stratinit/`. Endpoint paths are defined in `SIRestPaths` as string constants.

**Recommendation:**
- Split into domain controllers (see recommendation #6)
- Add `springdoc-openapi-starter-webmvc-ui` for automatic OpenAPI 3.0 docs (already in POM)
- Version the API: `/api/v1/games`, `/api/v1/units`, etc.
- Add `@Operation`, `@ApiResponse`, `@Schema` annotations to document request/response contracts
- Add request validation with `@Valid` and Bean Validation annotations on DTOs

**Key files:**
- `stratinit-rest/.../controller/StratInitController.java` (all 36 endpoints)
- `stratinit-core/.../remote/SIRestPaths.java` (path constants)

---

### 10. Domain Model Cleanup

**Status: NOT STARTED**

**Current state:**
- Composite keys using `EventKey` with manual `hashCode`/`equals` (`stratinit-core/.../model/EventKey.java`)
- Manual DTO conversion scattered across 10+ service classes (e.g., `new SIUnit(unit)`, `new SIGame(game)` in `NationSvc.java`, `CitySvc.java`, `UnitSvc.java`)
- 70+ hardcoded constants in `Constants.java` including game balance values, email addresses, and server version
- Unit stats hardcoded in `UnitBase.java` static initializer (attack, defense, movement, cost for 20+ unit types)

**Recommendation:**
- Replace manual DTO mapping with MapStruct interfaces — define `@Mapper` interfaces for each entity/DTO pair
- Move game balance constants (`TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES`, `HOURS_BETWEEN_UNIT_UPDATES`, etc.) to `application.yml` with `@ConfigurationProperties`
- Move unit definitions from `UnitBase` static initializer to a data file (YAML or database table)
- Replace `EventKey` manual hashCode/equals with Java 16+ `record` classes
- Move email addresses and server version to configuration

**Key files:**
- `stratinit-core/.../model/UnitBase.java` (618 lines, hardcoded unit stats)
- `stratinit-core/.../type/Constants.java` (70+ hardcoded values)
- `stratinit-core/.../model/EventKey.java` (manual hashCode/equals)
- `stratinit-server/.../svc/NationSvc.java`, `CitySvc.java`, `UnitSvc.java` (manual DTO conversion)

---

## Suggested Migration Order

```
Phase 1: Foundation (no user-facing changes)
  ├── #8  Replace deprecated dependencies
  ├── #4  Guava → Java streams (mechanical refactor) ✅ DONE
  └── #7  ConcurrentHashMap in DataCache             ✅ DONE

Phase 2: Backend modernization
  ├── #3  JWT authentication                         ✅ DONE
  ├── #6  Split god classes
  ├── #5  Replace Result/Request pattern
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
| `stratinit-server/.../request/write/PlayerWriteRequest.java` | Edit: added notification after write | 2 |
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
