# Full Game Playthrough — End-to-End Web Test

Play a complete game of Strategic Initiative from start to finish in the browser, fixing critical bugs as you go and logging ALL non-critical bugs as GitHub issues.

## Overview

You will:

1. Start the server and UI
2. Register/login, create a game with bots
3. Play through until victory or game end
4. Fix any blocking bugs inline (commit + push fixes)
5. **IMMEDIATELY file every non-blocking bug as a GitHub issue** — do not defer, do not batch

## Bug Filing — CRITICAL RULES

**Every bug MUST be filed as a GitHub issue the moment it is discovered.** Do not continue playing until the issue is filed. Do not "note it for later." Do not batch bugs. File immediately, then continue.

### How to File a Bug

When you observe incorrect behavior:

1. Take a screenshot for evidence
2. Check for duplicates first:
   ```bash
   gh issue list --label bug --state open
   ```
3. If not a duplicate, write the issue body to a temp file and create the issue:

   ```bash
   cat > /tmp/stratinit-bug.md << 'BUGEOF'
   ## Bug
   <What happened — be specific about the UI state, action taken, and incorrect result>

   ## Expected
   <What should happen based on game mechanics>

   ## Found in
   Full game playthrough — Day <N>

   ## Affected files
   - <file paths if known, otherwise omit this section>
   BUGEOF
   gh issue create --title "<brief title>" --label "bug" --body-file /tmp/stratinit-bug.md
   ```

4. Record the issue URL — you will need it for the final summary
5. **Then** continue playing

### What Counts as a Bug

File a bug for ANY of these:

- UI shows incorrect values (wrong unit stats, wrong city production, wrong relations)
- UI is missing expected elements (buttons, tabs, indicators)
- Actions produce wrong results (server accepts invalid action, rejects valid one)
- Visual glitches that affect gameplay (map not rendering, units not showing, fog-of-war wrong)
- Battle log shows incorrect or misleading messages
- WebSocket updates not arriving or causing stale state
- Bot turns stall or produce obviously wrong behavior
- Diplomacy changes not reflected in the UI
- Unit movement fails when it should succeed (or vice versa)

Do NOT file bugs for:

- Pure cosmetic issues (alignment, colors) that don't affect gameplay
- Known limitations

## Prerequisites

### Start the Server Stack

Start PostgreSQL, the Spring Boot backend, and the React dev server:

```bash
# Start PostgreSQL (if not running)
docker run -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres 2>/dev/null || docker start ken-postgres

# Build backend (skip tests for speed)
cd /Users/ken/git/stratinit/stratinit-master
mvn clean install -DskipTests

# Start Spring Boot server (port 8081) in background
mvn spring-boot:run -pl stratinit-rest &

# Start React dev server (port 5173) in background
cd stratinit-ui && npm run dev &
```

Wait for both to be ready:
- Backend: `http://localhost:8081/stratinit/version`
- Frontend: `http://localhost:5173`

### Check Existing Bugs

Before starting, review open bugs so you don't file duplicates:

```bash
gh issue list --label bug --state open
```

## Phase 1: Register, Login, and Create a Game

1. Open `http://localhost:5173` in Chrome
2. If not registered, go to `/register` and create a test account (e.g., username: `tester`, email: `test@test.com`, password: `tester123`)
3. Log in at `/login` with your credentials
4. Verify you land on `/games` and see the game list
5. **Create a new game** via the API (game creation is server-side):
   ```bash
   # Use the REST API to create a blitz game
   TOKEN=$(curl -s -X POST http://localhost:8081/stratinit/auth/login \
     -H 'Content-Type: application/json' \
     -d '{"username":"tester","password":"tester123"}' | jq -r '.token')

   # Join the game from the /games page in the browser
   ```
6. Navigate to `/games`, find the game in "Available Games", and click **Join**
7. You should see the **Game Lobby** screen
8. Click **"Add Bot"** 1-2 times to add bot opponents
9. Wait for the game to map (lobby polls every 5 seconds, shows status updates)
10. Take a screenshot once the game board loads

**Bug check**: Verify the map renders correctly with terrain, and the side panel shows 7 tabs (Sector, Units, Cities, Battle, Players, Mail, News). If anything is wrong, file a bug NOW.

## Phase 2: Play Through the Game

Play the game as the human player. Bots execute their turns automatically every 5 minutes (blitz-scaled). Your goal is to survive, expand, and reach a dominant position.

### Per-Turn Strategy

On each turn, follow this decision priority:

1. **Set city production** — Cities tab: assign unit types to build (infantry early, then diversify to naval/air/tech)
2. **Move units** — Click a sector with your units to select them, then click a target sector to move
3. **Capture neutral cities** — Move infantry/armor onto unoccupied cities
4. **Explore** — Send units toward unscouted (dark/unknown) territory
5. **Manage diplomacy** — Players tab: set relations with other nations (WAR, NEUTRAL, FRIENDLY, ALLIED)
6. **Build cities** — If you have Engineers, use "Build City" on suitable land sectors
7. **Read battle log** — Battle tab: check combat results, click coordinates to jump to the sector
8. **Check news** — News tab: review game events organized by day

### Unit Commands (Units Tab)

When units are selected at a sector:

- **Move**: Click target sector on the map
- **Cancel Move**: Remove pending move orders
- **Build City** (Engineers only): Build a new city at the current sector
- **Switch Terrain** (Engineers only): Change terrain type
- **Disband**: Remove selected units
- **Cede to Ally**: Transfer units to an allied nation (dropdown)

### City Management (Cities Tab)

For each city you own:

- **Build**: Select the unit type currently being produced
- **Next Build**: Queue the next unit type after current finishes

Unit types are gated by tech level and coastal access (naval units require coastal cities).

### Per-Turn Bug Checks

After EVERY action, verify:

- The map updated to reflect your move/action
- Side panel shows correct unit positions and stats
- No error messages or console errors appeared
- Battle log entries are accurate after combat

After EVERY game day transition, verify:

- New units appear at cities that completed production
- Bot nations have moved/acted (check map changes)
- WebSocket updates are arriving (state refreshes automatically)

**If anything is wrong, STOP and file a bug issue before continuing.**

## Phase 3: Bug Handling

### Blocking Bugs (Game Cannot Continue)

If you encounter a bug that prevents the game from continuing:

1. Take a screenshot of the error state
2. Investigate the root cause in the codebase
3. Fix the bug in the source code
4. Run `mvn clean install -DskipTests` to verify the fix compiles
5. Run relevant tests: `mvn test -pl <module> -Dtest=<TestClass>`
6. Commit and push the fix:
   ```
   git add <files> && git commit -m "fix: <description>" && git push
   ```
7. Restart the server: kill the Spring Boot process and re-run `mvn spring-boot:run -pl stratinit-rest`
8. Refresh the browser and try to continue the game
9. If the game state is corrupted and unrecoverable, create a new game from Phase 1

### Non-Blocking Bugs (Game Can Continue)

**MANDATORY**: File a GitHub issue IMMEDIATELY using the process in "Bug Filing — CRITICAL RULES" above. Do not skip this step. Do not defer filing to later. File the issue, record the URL, then continue playing.

## Phase 4: Bug Audit

**Before writing the final summary, you MUST complete this audit.**

1. List every bug you encountered during the playthrough — blocking fixes AND non-blocking observations
2. For each bug, verify it was either:
   - **Fixed inline** (has a git commit) — note the commit hash, OR
   - **Filed as a GitHub issue** — note the issue number/URL
3. If ANY bug was observed but NOT filed, file it NOW:
   ```bash
   gh issue list --label bug --state open
   ```
   Check the list, then file any missing bugs.
4. Print a final bug summary table showing every bug and its disposition.

**Do NOT proceed to the Game Completion summary until every bug has been accounted for.**

## Phase 5: Game Completion

When the game ends (or you've played enough turns to thoroughly test):

1. Take a screenshot of the final game state
2. Check the leaderboard at `/leaderboard` and rankings at `/rankings`
3. View game stats at `/stats` if available
4. Print the complete summary:

```
## Playthrough Summary

### Game Result
- Game ID: <N>
- Days played: <N>
- Nations: <list>
- Final state: <won/lost/ongoing>

### Bugs Fixed Inline (Blocking)
| Commit | Description |
|--------|-------------|
| <hash> | <what was fixed> |

### Bugs Filed as GitHub Issues (Non-Blocking)
| Issue | Title | Day Found |
|-------|-------|-----------|
| #<N>  | <title> | Day <N> |

### Bug Audit
- Total bugs found: <N>
- Bugs fixed inline: <N>
- Bugs filed as issues: <N>
- Bugs unaccounted for: 0 (MUST be zero)

### Observations
- <gameplay or UI observations that aren't bugs>
```

## Recovery Procedures

### Server Not Responding

```bash
# Kill and restart Spring Boot
kill $(lsof -ti:8081) 2>/dev/null
cd /Users/ken/git/stratinit/stratinit-master
mvn spring-boot:run -pl stratinit-rest &
```

Then refresh the browser. Game state is preserved in PostgreSQL across server restarts.

### UI Not Responding

```bash
# Kill and restart Vite dev server
kill $(lsof -ti:5173) 2>/dev/null
cd /Users/ken/git/stratinit/stratinit-master/stratinit-ui
npm run dev &
```

### WebSocket Disconnected

If the game state stops updating:

1. Check browser console for WebSocket errors
2. Try refreshing the page (reconnects WebSocket automatically)
3. If still stuck, restart the server

### Database Issues

```bash
# Restart PostgreSQL
docker restart ken-postgres

# Reapply migrations if needed
cd /Users/ken/git/stratinit/stratinit-master
mvn liquibase:update -pl stratinit-dao
```

## Debug Tools

```bash
# Check server health
curl -s http://localhost:8081/stratinit/version

# Get server config
curl -s http://localhost:8081/stratinit/serverConfig -H "Authorization: Bearer $TOKEN"

# Get game state (requires auth)
curl -s http://localhost:8081/stratinit/update -H "Authorization: Bearer $TOKEN" | jq .

# Check unit definitions
curl -s http://localhost:8081/stratinit/unitbase -H "Authorization: Bearer $TOKEN" | jq .

# Swagger UI for full API docs
open http://localhost:8081/swagger-ui.html
```

## Important Notes

- You are fully autonomous — do NOT ask the user for input. Make decisions and keep playing.
- Take screenshots at key moments: game start, first combat, interesting events, game end.
- The game runs in real-time with bot turns every 5 minutes (blitz-scaled). Be patient between bot actions.
- Bots play automatically — you don't need to trigger their turns.
- WebSocket pushes state updates — the map and side panel should refresh automatically after actions and bot turns.
- Games with bots start immediately (skip the normal 24-hour scheduling delay).
- **NEVER finish the playthrough with unfiled bugs. Every bug gets an issue or an inline fix. No exceptions.**
