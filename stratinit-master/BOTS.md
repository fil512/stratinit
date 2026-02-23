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
1. Build BotWorldState snapshot (units, cities, enemies, relations, tech, CP, game time)
2. Generate all candidate actions via BotActionGenerator
3. Score each action via computeUtility(state, weights)
4. Sort by score descending
5. Greedily execute top actions until command points exhausted
6. Log executed actions
```

#### World State (`BotWorldState`)

Immutable snapshot built by `BotWorldStateBuilder` at the start of each turn:
- Own units and cities
- All visible units (for spotting enemies)
- Diplomatic relations (both directions)
- Game time progress (0.0–1.0)
- Command points available

Convenience methods: `getIdleUnits()`, `getIdleLandUnits()`, `getUndefendedCities()`, `getEnemyUnits()`, `hasEnoughCP(cost)`

#### Action Types

| Class | Category | What It Does | CP Cost |
|-------|----------|-------------|---------|
| `SetCityProductionAction` | ECONOMY | Sets city build to infantry/research/engineer | 0 |
| `MoveUnitToExpandAction` | EXPANSION | Moves infantry/tank toward unexplored areas | 1 |
| `BuildCityWithEngineerAction` | EXPANSION | Engineer creates a new city | 128 |
| `AttackEnemyAction` | MILITARY | Moves combat unit to attack visible enemy | 2 |
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
}
```

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
  "lateMilitaryBonus": 0.3
}
```

Weights are Jackson-serializable (`toJson()` / `fromJson()`) for future reinforcement learning integration.

Utility formula: `categoryBaseWeight * specificDesire * contextMultipliers * timeModifier`

Time modifiers shift bot behavior during the game:
- **Early game** (< 30% elapsed): expansion and economy bonus
- **Late game** (> 50% elapsed): military bonus

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
  server/bot/action/BotAction.java     — Action interface
  server/bot/action/BotActionCategory.java         — Category enum
  server/bot/action/SetCityProductionAction.java    — Economy action
  server/bot/action/MoveUnitToExpandAction.java     — Expansion action
  server/bot/action/BuildCityWithEngineerAction.java — Expansion action
  server/bot/action/AttackEnemyAction.java          — Military action
  server/bot/action/DefendCityAction.java           — Defense action
  server/bot/action/SetRelationAction.java          — Diplomacy action
  resources/bot-weights.json           — Default weight values

stratinit-rest/
  controller/GameController.java       — POST /stratinit/add-bot endpoint

stratinit-ui/
  src/api/game.ts                      — addBot() API function
  src/pages/GamePage.tsx               — "Add Bot" button in GameLobby
```

## Future Improvements

- **Reinforcement learning**: Swap `bot-weights.json` with trained weights. `BotWeights` is already Jackson-serializable for this purpose.
- **More action types**: Naval operations, air support, satellite launches, ICBM strikes, transport loading
- **Difficulty levels**: Multiple weight profiles (easy/medium/hard)
- **Smarter pathfinding**: Use the `stratinit-graph` module for optimal movement paths
- **Fog-of-war awareness**: Currently bots see all units; could be restricted to their actual vision
- **Coordination**: Multi-unit attack groups, combined arms tactics
