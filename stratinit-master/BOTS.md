# Bot Players

Bot players allow a single human to start and play a game by filling the remaining slots with AI opponents. Bots run entirely server-side, calling domain services directly without going through REST/HTTP/JWT.

## How It Works

1. A human player joins a game from the lobby
2. They click **"Add Bot"** to add AI opponents (up to the game's capacity)
3. When the minimum player count is reached (including bots), the game starts **immediately** — skipping the normal 24-hour scheduling delay
4. Bots execute actions every 5 minutes (blitz-scaled) via the event system

## Architecture

### Data Model

- `Player.bot` — boolean column distinguishing bot accounts from human players
- `Player.makeBotPlayer(name)` — factory method creating bot accounts (no password, email disabled)
- Bot names: "Bot Alpha", "Bot Bravo", "Bot Charlie", etc. (reused across games)

### Join Flow

`POST /stratinit/add-bot` → `GameController.addBot()` → `BotService.addBotToGame(gameId)`

`BotService` handles:
- Creating or reusing bot `Player` accounts by name
- Calling `GameService.joinGame()` to add the bot as a nation
- When enough players join: setting start time to now, mapping the game, and starting it immediately via `GameStartupService`

### Execution Scheduling

Bot turns follow the existing event system pattern (`TechUpdateEvent` / `EventUpdate`):

```
BotTurnEvent (periodic, 5 min blitz-scaled)
  → StratInitUpdater.executeBotTurns(gameId)
    → BotTurnEventUpdate.executeWrite()  [synchronized on GameCache]
      → BotExecutor.executeTurn(nation)   [for each bot nation]
```

`BotTurnEvent` uses a custom `BotTurnUpdatable` inner class to get a distinct `EventKey` from `TechUpdateEvent` (both reference the same `Game`).

The event is scheduled in `EventQueue.scheduleStartedGameEvents()` only for games that have bot nations.

### Utility AI Decision Engine

The bot AI uses a **Utility AI** pattern — each possible action is scored by a utility function, and the highest-scoring actions are executed greedily until command points are exhausted.

#### Turn Execution (`BotExecutor`)

```
1. Build BotWorldState snapshot (units, cities, enemies, relations, tech, CP, game time, coastal cities, enemy cities, world terrain, scouted coords, neutral cities)
2. Generate all candidate actions via BotActionGenerator (8 generator methods)
3. Score each action via computeUtility(state, weights)
4. Sort by score descending
5. Greedily execute top actions until command points exhausted, tracking usedUnitIds to prevent same unit acting twice
6. Log executed actions
```

#### World State (`BotWorldState`)

Immutable snapshot built by `BotWorldStateBuilder` at the start of each turn:
- Own units and cities
- Fog-of-war filtered visible units (via `UnitDao.getSeenUnits()` — only units within sight range of own units/satellites)
- Fog-of-war filtered enemy cities (via `CityDao.getSeenCities()` — only cities in sectors the nation has scouted)
- World terrain (via `SectorDao.getWorld()` — enables terrain-aware movement decisions)
- Scouted coordinates (via `SectorDao.getSectorsSeen()` — tracks explored territory)
- Neutral city locations (scouted sectors with `SectorType.NEUTRAL_CITY` — free captures)
- Diplomatic relations (both directions)
- Game time progress (0.0–1.0)
- Command points available

Bots respect the same fog-of-war rules as human players: they can only see enemy units within their units' sight range and enemy cities in sectors they have previously scouted. This means bots must scout before they can attack, making satellite launches and expansion genuinely useful for intelligence gathering.

Convenience methods: `getIdleUnits()`, `getIdleLandUnits()`, `getIdleNavalUnits()`, `getIdleAirUnits()`, `getLaunchableUnits()`, `getUndefendedCities()`, `getEnemyUnits()`, `getEnemyCities()`, `isCoastalCity(city)`, `hasEnoughCP(cost)`, `getWorld()`, `isExplored(coords)`, `getNeutralCityCoords()`, `getHomeIslandId()`, `getLoadedTransports()`, `getLandUnitsAtSea()`

Coordination queries (used by attack actions to compute multi-unit bonuses):
- `countLandUnitsInRangeOf(target)` — idle land units within mobility range of target
- `countAirUnitsInRangeOf(target)` — idle air units (attack > 0) within mobility range
- `hasNearbyTransport(target, radius)` — any friendly transport within radius
- `hasLandThreatNear(target)` — shorthand for `countLandUnitsInRangeOf > 0`

Pre-computed data (built by `BotWorldStateBuilder`):
- **Coastal city coords**: Cities adjacent to water sectors (enables naval unit production gating)
- **Enemy cities**: Cities owned by nations at WAR, filtered to only those in scouted sectors
- **Scouted coords**: All sector coordinates the nation has ever seen (for frontier detection)
- **Neutral city coords**: Scouted sectors containing neutral cities (for targeted capture)

#### Action Types

| Class | Category | What It Does | CP Cost |
|-------|----------|-------------|---------|
| `SetCityProductionAction` | ECONOMY | Sets city build to any of 23 unit types (gated by tech + coastal) | 0 |
| `MoveUnitToExpandAction` | EXPANSION | Moves land or naval unit toward frontier (unscouted territory) | 1 |
| `CaptureNeutralCityAction` | EXPANSION | Moves land unit to capture a scouted neutral city | 1 |
| `BuildCityWithEngineerAction` | EXPANSION | Engineer creates a new city | 128 |
| `AttackEnemyAction` | MILITARY | Moves land unit to attack enemy; coordination bonus when allies converge, partial air synergy | 2 |
| `AttackNavalAction` | MILITARY | Moves naval unit to attack enemy; escort bonus near friendly transports | 2 |
| `AttackWithAirAction` | MILITARY | Air strike/bomb; air support bonus when ground forces can follow up | 2 |
| `LoadTransportAction` | EXPANSION | Moves transport/carrier to pick up idle land units | 1 |
| `MoveTransportToTargetAction` | EXPANSION | Sails loaded transport toward landing zone on another island | 1 |
| `DisembarkUnitAction` | EXPANSION | Moves land unit from water onto adjacent land sector | 1 |
| `LaunchSatelliteAction` | EXPANSION | Launches satellite toward map center for vision | 8 |
| `LaunchICBMAction` | MILITARY | Launches ICBM at enemy city | 32 |
| `DefendCityAction` | DEFENSE | Moves idle unit to undefended own city | 1 |
| `SetRelationAction` | DIPLOMACY | Mirrors diplomatic stance (friendly→friendly, war→war) | 0 |

Each action implements `BotAction`:
```java
public interface BotAction {
    BotActionCategory getCategory();          // ECONOMY, EXPANSION, MILITARY, DEFENSE, DIPLOMACY
    double computeUtility(BotWorldState, BotWeights);  // Score this action
    boolean execute();                         // Perform the action
    int getCommandPointCost();                 // CP cost
    String describe();                         // For logging
    default Integer getInvolvedUnitId();       // Prevents same unit acting twice per turn
}
```

**City production gating**: `BotActionGenerator` suggests all 23 unit types but gates them:
- **Land units** (INFANTRY, TANK, ENGINEER, RESEARCH): any city
- **Naval units** (TRANSPORT, PATROL, DESTROYER, BATTLESHIP, SUBMARINE, CARRIER, CRUISER, SUPPLY): coastal cities only (`isCoastalCity()`)
- **Air units** (FIGHTER, HEAVY_BOMBER, NAVAL_BOMBER, HELICOPTER, CARGO_PLANE, ZEPPELIN): any city (auto-converts to airport)
- **Strategic units** (SATELLITE, ICBM_1/2/3): any city (auto-converts to tech center)

Tech requirements are enforced in `SetCityProductionAction.computeUtility()` (returns 0 if tech too low).

**Action generators**: `BotActionGenerator.generateActions()` calls 8 generator methods:
- `generateCityProductionActions()` — all unit types with coastal/tech gating
- `generateExpansionActions()` — frontier-based land + naval exploration, neutral city capture, engineer city building
- `generateMilitaryActions()` — land-vs-land combat
- `generateNavalActions()` — naval combat + transport loading + transport destination planning + disembark
- `generateAirActions()` — air strikes + city bombing
- `generateStrategicActions()` — satellite launch (map center) + ICBM launch (enemy cities)
- `generateDefenseActions()` — move to undefended cities
- `generateDiplomacyActions()` — mirror diplomatic stance

#### Weights (`BotWeights`)

All utility calculations use configurable weights loaded from `bot-weights.json`:

```json
{
  "economyBaseWeight": 1.0,
  "techCentreDesire": 0.8,
  "infantryDesire": 0.7,
  "engineerDesire": 0.8,
  "expansionBaseWeight": 0.9,
  "distancePenalty": 0.1,
  "buildCityDesire": 0.7,
  "militaryBaseWeight": 0.7,
  "attackWeakDesire": 0.9,
  "hpAdvantageFactor": 0.5,
  "defenseBaseWeight": 0.8,
  "cityDefenseDesire": 0.9,
  "undefendedPenalty": 1.0,
  "diplomacyBaseWeight": 0.3,
  "earlyExpansionBonus": 0.5,
  "lateMilitaryBonus": 0.3,
  "navalBaseWeight": 0.6,
  "navalCombatDesire": 0.7,
  "transportLoadDesire": 0.5,
  "transportDestinationDesire": 0.9,
  "disembarkDesire": 1.0,
  "airStrikeDesire": 0.7,
  "satelliteLaunchDesire": 0.5,
  "icbmLaunchDesire": 0.8,
  "neutralCityCaptureDesire": 1.2,
  "tankDesire": 0.6,
  "coordinationBonus": 0.3,
  "airSupportBonus": 0.4,
  "navalEscortBonus": 0.5,
  "massAttackThreshold": 2.0
}
```

Weights are Jackson-serializable (`toJson()` / `fromJson()`) for future reinforcement learning integration.

Utility formula: `categoryBaseWeight * specificDesire * contextMultipliers * timeModifier`

Time modifiers shift bot behavior during the game:
- **Early game** (< 30% elapsed): expansion and economy bonus
- **Late game** (> 50% elapsed): military bonus

#### Multi-Unit Coordination

Coordination emerges through utility bonuses rather than explicit multi-unit planning. When multiple friendly units can reach the same target, each gets a bonus that pushes coordinated attacks above isolated ones in the greedy sort:

- **Land coordination** (`AttackEnemyAction`): When `alliesInRange >= massAttackThreshold - 1`, each ally adds `coordinationBonus` to utility. Three infantry converging on one enemy each score higher than a solo attacker.
- **Air support** (`AttackWithAirAction`): Air strikes targeting sectors where ground forces can also reach get `airSupportBonus`. Since the bonus makes air utility exceed ground utility, the greedy executor picks air first (softening), then ground (finishing).
- **Naval escort** (`AttackNavalAction`): Naval combat near friendly transports (within 3 sectors) gets `navalEscortBonus`, causing destroyers to prioritize clearing threats around transports.
- **Ground-side air synergy** (`AttackEnemyAction`): Ground attacks also get a partial `airSupportBonus * 0.5` when air units can reach the same target, further rewarding combined-arms convergence.

No changes to `BotAction` interface, `BotExecutor`, or `BotActionGenerator` are needed — coordination is purely a scoring change.

### Bot-Specific Behavior

- **Email**: Bots are skipped in `GameService.scheduleGame()` email notifications
- **Scoring**: Bots are skipped in `GameService.score()` — no wins/played stats recorded
- **Immediate start**: `BotService.startBotGameImmediately()` sets start time to now, maps, and starts the game

## File Layout

```
stratinit-core/
  Player.java                          — bot field, makeBotPlayer()
  Constants.java                       — BOT_TURN_INTERVAL_SECONDS
  SIRestPaths.java                     — ADD_BOT path

stratinit-dao/
  db/changelog/migrations/V20260222000000.sql  — ALTER TABLE player ADD COLUMN bot

stratinit-server/
  server/service/BotService.java       — Bot creation, game join, immediate start
  server/service/GameService.java      — Skip email/scoring for bots
  server/event/BotTurnEvent.java       — Periodic event (5 min, blitz-scaled)
  server/event/update/BotTurnEventUpdate.java  — Synchronized bot turn execution
  server/event/EventFactory.java       — getBotTurnEvent() lookup
  server/event/update/EventUpdateFactory.java  — getBotTurnEventUpdate() lookup
  server/event/svc/EventQueue.java     — Schedule BotTurnEvent for bot games
  server/event/svc/StratInitUpdater.java — executeBotTurns() method
  server/bot/BotExecutor.java          — Turn orchestration
  server/bot/BotActionGenerator.java   — Candidate action generation
  server/bot/BotWorldState.java        — Immutable game state snapshot
  server/bot/BotWorldStateBuilder.java — Builds BotWorldState from DAOs
  server/bot/BotWeights.java           — Tunable weight parameters
  server/bot/BotWeightsConfig.java     — Spring config for weight loading
  server/bot/action/BotAction.java     — Action interface (with getInvolvedUnitId())
  server/bot/action/BotActionCategory.java         — Category enum
  server/bot/action/SetCityProductionAction.java    — Economy: all 23 unit types
  server/bot/action/MoveUnitToExpandAction.java     — Expansion: frontier-based land + naval movement
  server/bot/action/CaptureNeutralCityAction.java   — Expansion: capture neutral cities
  server/bot/action/BuildCityWithEngineerAction.java — Expansion: engineer builds city
  server/bot/action/LoadTransportAction.java        — Expansion: transport picks up land units
  server/bot/action/MoveTransportToTargetAction.java — Expansion: sail loaded transport to other island
  server/bot/action/DisembarkUnitAction.java        — Expansion: land unit steps off transport onto land
  server/bot/action/LaunchSatelliteAction.java      — Expansion: satellite launch for vision
  server/bot/action/AttackEnemyAction.java          — Military: land combat
  server/bot/action/AttackNavalAction.java          — Military: naval combat
  server/bot/action/AttackWithAirAction.java        — Military: air strikes + city bombing
  server/bot/action/LaunchICBMAction.java           — Military: ICBM at enemy cities
  server/bot/action/DefendCityAction.java           — Defense action
  server/bot/action/SetRelationAction.java          — Diplomacy action
  resources/bot-weights.json           — Default weight values

stratinit-rest/
  controller/GameController.java       — POST /stratinit/add-bot endpoint

stratinit-ui/
  src/api/game.ts                      — addBot() API function
  src/pages/GamePage.tsx               — "Add Bot" button in GameLobby
```

## Reinforcement Learning Training

An evolutionary hill-climbing framework tunes `BotWeights` by simulating games between bot populations.

### How It Works

1. Start with a population of 8 weight configurations (seed + 7 mutations)
2. Each generation runs 2 simulated 4-bot blitz games, randomly picking 4 configs per game
3. Score each bot: `10 * citiesOwned + 1 * aliveUnits + 0.5 * tech`
4. Keep the top 4 performers, replace the bottom 4 with mutated/crossed winners
5. Repeat for N generations (default 10, test uses 3)
6. Save best weights and score history to `training-results/`

### Synchronous Game Simulation

`TrainingGameSimulator` runs a complete game synchronously without the event system:

1. Creates a blitz game with 4 bot players
2. Maps the game and calls `GameStartupService.initializeGameState()` (no event scheduling)
3. Sets start time in the past (so `hasStarted()` returns true) and ends far in the future
4. Runs 100 turns, each advancing simulated time by one tick interval:
   - Processes city builds using `CityBuilderService.buildUnit(city, simDate)`
   - Processes unit updates using `UnitService.updateUnit(unit, simDate)`
   - Updates tech/CP via `GameService.updateGame(game, simDate)`
   - Executes bot turns via `BotExecutor.executeTurn(nation, weights, simulatedTime)`
5. Scores all nations and returns results

The tick interval is computed using `UpdateCalculator.shrinkTime(blitz=true, ...)` to match blitz timing.

### Key Modifications for Training

- **`BotWorldState`**: Added constructor accepting `long nowMillis` for simulated time (replaces `System.currentTimeMillis()`)
- **`BotWorldStateBuilder`**: Added `build(Nation, long simulatedTimeMillis)` overload
- **`BotExecutor`**: Added `executeTurn(Nation, BotWeights)` and `executeTurn(Nation, BotWeights, long simulatedTimeMillis)` overloads
- **`GameStartupService`**: Extracted `initializeGameState(Game, boolean)` from `startGame()` — initializes game state (cities, units, fog) without scheduling timer events

### Mutation

`WeightMutator` operates via reflection on `BotWeights` public double fields:
- **Mutation**: Each weight has 30% chance of Gaussian noise (stddev=0.2), clamped to [0.01, 3.0]
- **Crossover**: Uniform crossover picks each weight randomly from parent A or B

### Running Training

```bash
# Run the training test (3 generations, ~10 seconds)
mvn test -pl stratinit-server -Dtest=BotRLTrainingTest

# Results saved to:
#   training-results/best-weights.json    — best weight configuration
#   training-results/score-history.json   — best score per generation
```

Resuming training loads previous best weights from `training-results/best-weights.json` as the seed.

### Training File Layout

```
stratinit-server/
  server/bot/training/
    TrainingSession.java           — Evolutionary training orchestration
    TrainingGameSimulator.java     — Synchronous game simulation
    TrainingScorer.java            — Nation performance scoring
    WeightMutator.java             — Gaussian mutation + uniform crossover
    TrainingGameResult.java        — Single game result record
    TrainingResult.java            — Full session result record

  (test)
  server/bot/training/
    BotRLTrainingTest.java         — Integration test entry point
```

## Future Improvements

- **Difficulty levels**: Multiple weight profiles (easy/medium/hard)
- **Allied vision sharing**: Bots could share fog-of-war with allies via `getTeamSeenUnits()`
- **Larger training runs**: More generations, larger populations, parallel game simulation
- **Alternative algorithms**: Gradient-free optimization (CMA-ES), neuroevolution
