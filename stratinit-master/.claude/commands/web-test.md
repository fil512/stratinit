# Full Game Playthrough — End-to-End Web Test

Use **Claude in Chrome browser automation** to play a complete game of Strategic Initiative, testing every UI feature along the way. All interactions MUST go through the browser — clicking buttons, filling forms, reading the page. Do NOT use curl or direct API calls for any game actions.

## Overview

You will:

1. Start the server and UI (bash — this is the only phase that uses the terminal)
2. Use Chrome to register/login, create a game with bots, and play through it
3. Test every UI feature: all 7 side panel tabs, unit commands, city management, diplomacy, mail, news
4. Fix any blocking bugs inline (commit + push fixes)
5. File every non-blocking bug as a GitHub issue immediately when discovered

## Bug Filing — CRITICAL RULES

**Every bug MUST be filed as a GitHub issue the moment it is discovered.** Do not continue playing until the issue is filed.

### How to File a Bug

1. Take a screenshot with `mcp__claude-in-chrome__computer` (action: screenshot)
2. Check for duplicates: `gh issue list --label bug --state open`
3. File the issue:
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
4. Record the issue URL, then continue playing

### What Counts as a Bug

- UI shows incorrect values (wrong unit stats, wrong city production, wrong relations)
- UI is missing expected elements (buttons, tabs, indicators)
- Actions produce wrong results (server accepts invalid action, rejects valid one)
- Visual glitches that affect gameplay (map not rendering, units not showing, fog-of-war wrong)
- Battle log shows incorrect or misleading messages
- WebSocket updates not arriving or causing stale state
- Bot turns stall or produce obviously wrong behavior
- Diplomacy changes not reflected in the UI
- Unit movement fails when it should succeed (or vice versa)

Do NOT file bugs for: pure cosmetic issues (alignment, colors) that don't affect gameplay.

## Phase 1: Start the Server Stack (Terminal Only)

This is the ONLY phase that uses bash commands. Everything after this is browser automation.

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

Wait for both to be ready before proceeding to browser automation.

Also check existing bugs so you don't file duplicates:
```bash
gh issue list --label bug --state open
```

## Phase 2: Browser Setup

1. Call `mcp__claude-in-chrome__tabs_context_mcp` to get available tabs
2. Create a new tab with `mcp__claude-in-chrome__tabs_create_mcp`
3. Navigate to `http://localhost:5173` using `mcp__claude-in-chrome__navigate`

## Phase 3: Register and Login

### Registration (if needed)
1. Navigate to `http://localhost:5173/register`
2. Use `find` to locate form fields, then `form_input` to fill them:
   - Username: `tester`
   - Email: `test@test.com`
   - Password: `tester123`
   - Confirm Password: `tester123`
3. Click the "Register" button
4. **Verify**: Page redirects to `/login` or shows success message. If error says username exists, proceed to login.

### Login
1. Navigate to `http://localhost:5173/login`
2. Fill the login form:
   - Username field (`id="username"`): `tester`
   - Password field (`id="password"`): `tester123`
3. Click the "Login" button
4. **Verify**: Page redirects to `/games`. Take a screenshot.

### Bug check
- Verify the nav bar shows: "Strategic Initiative", "Games", "Leaderboard", "Rankings", "Stats", "Settings", "Logout"
- Verify the games page has "My Games" and "Available Games" sections

## Phase 4: Create and Join a Game

1. On `/games`, look for games in the "Available Games" table
2. If a game exists, click its **"Join"** button
3. If no games are available, you may need to check if there's a create game mechanism or join an existing one
4. **Verify**: After joining, you should see the **Game Lobby** screen with:
   - Game name and status
   - Player count, Islands, Created date
   - "Add Bot" button
5. Click **"Add Bot"** three times to add 3 bot opponents (minimum 4 players required to start a game)
6. Take a screenshot of the lobby
7. Wait for the game to start — the lobby auto-polls every 5 seconds
8. Once the game is mapped, the page should transition to the game board
9. Take a screenshot of the initial game board

### Bug check
- Map renders with colored terrain (blue water, green land, gray wasteland, black unknown/fog)
- Side panel appears on the right with 7 tab buttons: Sector, Units, Cities, Battle, Players, Mail, News
- Your nation's units and cities are visible on the map

## Phase 5: Test All UI Features While Playing

### 5.1 — Sector Tab (default)
1. Click on a sector containing your units on the map canvas
2. **Verify** the Sector tab shows: coordinates, terrain type, units present
3. Click on a sector with your city
4. **Verify**: City info appears with "Build:" and "Next Build:" dropdowns
5. Take a screenshot

### 5.2 — Units Tab
1. Click the **"Units"** tab button
2. Click on a sector with your units on the map
3. **Verify**: Unit cards appear showing type, HP, mobility, ammo, fuel
4. Click a unit card to select it (should highlight blue)
5. Click **"Select All"** button — verify all units highlight
6. **Verify**: Action buttons appear: "Cancel Move", "Build City" (if engineer), "Switch Terrain" (if engineer), "Disband"
7. With units selected, click a neighboring land sector to **move** them
8. **Verify**: Movement line (yellow) appears on the map from source to destination
9. Click **"Cancel Move"** to cancel the pending move
10. **Verify**: Movement line disappears
11. Take a screenshot

### 5.3 — Cities Tab
1. Click the **"Cities"** tab button
2. **Verify**: All your cities are listed with "Build:" and "Next:" dropdowns
3. Click a city name — **verify** the map centers on that city's sector
4. Change a city's production using the "Build:" dropdown (e.g., select INFANTRY or another unit type)
5. **Verify**: Dropdown updates to show the new selection
6. Change the "Next:" build queue for a city
7. Take a screenshot

### 5.4 — Battle Tab
1. Click the **"Battle"** tab button
2. Early in the game this may show "No battle log entries." — that's OK
3. After combat occurs (move units into enemy territory or wait for bot attacks), check back
4. **Verify**: Battle entries show attacker vs defender, damage numbers, coordinates
5. Click a battle entry's coordinates — **verify** the map jumps to that sector
6. Take a screenshot when battle entries exist

### 5.5 — Players Tab
1. Click the **"Players"** tab button
2. **Verify**: All nations listed with: name, cities count, power, tech level
3. Your nation shows "(You)" label
4. For another nation, change the relation dropdown (e.g., set to FRIENDLY or ALLIED)
5. **Verify**: The relation updates and the display reflects the change
6. **Verify**: "Concede" button exists at the bottom
7. Do NOT click concede (it would end your game)
8. Take a screenshot

### 5.6 — Mail Tab
1. Click the **"Mail"** tab button
2. **Verify**: Sub-tabs exist: Inbox, Sent, News (Announcements), New (Compose)
3. Click **"New"** (Compose) sub-tab
4. **Verify**: Compose form appears with: "To:" dropdown, "Subject:" input, "Body:" textarea, "Send" button
5. Select a recipient from the "To:" dropdown (e.g., "All (broadcast)" or a specific nation)
6. Fill in Subject: "Test message"
7. Fill in Body: "Hello from the test playthrough"
8. Click **"Send"**
9. **Verify**: Message sends successfully (no error)
10. Click **"Sent"** sub-tab — **verify** your sent message appears
11. Click **"Inbox"** sub-tab to check for any received messages
12. Take a screenshot

### 5.7 — News Tab
1. Click the **"News"** tab button
2. **Verify**: News entries appear organized by day number
3. **Verify**: Entries have color-coded category labels (Bulletin, First, Diplomacy, etc.)
4. Take a screenshot

## Phase 6: Play the Game

With all tabs tested, now play through the game strategically. Use the browser for ALL actions.

### Per-Turn Loop
1. **Set city production** — Cities tab: change build dropdowns to produce useful units (INFANTRY early, diversify later)
2. **Move units** — Click sector with your units (Units tab to select), then click target sector to move
3. **Capture neutral cities** — Move infantry onto unoccupied cities (grey/neutral colored)
4. **Explore** — Send units toward dark/unknown territory
5. **Check battle log** — Battle tab after any combat
6. **Check news** — News tab periodically for game events
7. **Manage diplomacy** — Players tab to adjust relations as needed

### Between Turns
- Bots execute automatically every ~5 minutes (blitz-scaled)
- WebSocket should push updates automatically — watch for map changes
- If the state seems stale, refresh the page in the browser
- Take screenshots at interesting moments: first combat, city captures, interesting map states

### Per-Action Bug Checks
After EVERY action, verify:
- Map updated to reflect the action
- Side panel shows correct state
- Check browser console for errors: `mcp__claude-in-chrome__read_console_messages` with pattern `error|Error|ERR`
- Battle log is accurate after combat

**If anything is wrong, STOP and file a bug issue before continuing.**

## Phase 7: Test Remaining Pages

During or after gameplay, test the other pages via the nav bar:

### Leaderboard (`/leaderboard`)
1. Click "Leaderboard" in the nav bar
2. **Verify**: Table shows: Rank, Player, Wins, Played, Win %
3. Take a screenshot

### Rankings (`/rankings`)
1. Click "Rankings" in the nav bar
2. **Verify**: Rankings table loads
3. Take a screenshot

### Stats (`/stats`)
1. Click "Stats" in the nav bar
2. **Verify**: Stats page loads with charts (if data exists) or appropriate empty state
3. Take a screenshot

### Settings (`/settings`)
1. Click "Settings" in the nav bar
2. **Verify**: Profile info displays (username, member date, games played, wins)
3. **Verify**: Email field, notification checkbox, password change fields all present
4. Take a screenshot
5. Navigate back to the game: click "Games" then click your game

## Phase 8: Bug Handling

### Blocking Bugs (Game Cannot Continue)
1. Take a screenshot of the error state
2. Check browser console: `mcp__claude-in-chrome__read_console_messages`
3. Investigate the root cause in the codebase (use Grep, Read tools)
4. Fix the bug in the source code
5. `mvn clean install -DskipTests` to verify the fix compiles
6. Run relevant tests: `mvn test -pl <module> -Dtest=<TestClass>`
7. Commit and push: `git add <files> && git commit -m "fix: <description>" && git push`
8. Restart the server if needed
9. Refresh the browser and continue

### Non-Blocking Bugs
File a GitHub issue IMMEDIATELY (see Bug Filing rules above), then continue playing.

## Phase 9: Bug Audit

**Before writing the final summary, complete this audit.**

1. List every bug encountered — blocking fixes AND non-blocking observations
2. For each bug, verify it was either:
   - **Fixed inline** (has a git commit) — note the commit hash, OR
   - **Filed as a GitHub issue** — note the issue number/URL
3. File any missing bugs NOW
4. Print a final bug summary table

## Phase 10: Game Completion

When the game ends or you've thoroughly tested all features:

1. Take a final screenshot of the game state
2. Navigate to `/leaderboard` and `/rankings` — take screenshots
3. Print the complete summary:

```
## Playthrough Summary

### Game Result
- Game ID: <N>
- Days played: <N>
- Nations: <list>
- Final state: <won/lost/ongoing>

### Features Tested
| Feature | Status | Notes |
|---------|--------|-------|
| Login/Register | ✅/❌ | |
| Game Lobby | ✅/❌ | |
| Map Rendering | ✅/❌ | |
| Sector Tab | ✅/❌ | |
| Units Tab | ✅/❌ | |
| Cities Tab | ✅/❌ | |
| Battle Tab | ✅/❌ | |
| Players Tab | ✅/❌ | |
| Mail Tab | ✅/❌ | |
| News Tab | ✅/❌ | |
| Unit Movement | ✅/❌ | |
| City Production | ✅/❌ | |
| Diplomacy | ✅/❌ | |
| WebSocket Updates | ✅/❌ | |
| Leaderboard Page | ✅/❌ | |
| Rankings Page | ✅/❌ | |
| Stats Page | ✅/❌ | |
| Settings Page | ✅/❌ | |

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
kill $(lsof -ti:8081) 2>/dev/null
cd /Users/ken/git/stratinit/stratinit-master
mvn spring-boot:run -pl stratinit-rest &
```
Then refresh the browser.

### UI Not Responding
```bash
kill $(lsof -ti:5173) 2>/dev/null
cd /Users/ken/git/stratinit/stratinit-master/stratinit-ui
npm run dev &
```

### WebSocket Disconnected
1. Check console: `mcp__claude-in-chrome__read_console_messages` with pattern `WebSocket|socket|ws`
2. Refresh the page in the browser
3. If still stuck, restart the server

## Important Notes

- **ALL game interactions happen through the browser** — never use curl/API calls for game actions
- You are fully autonomous — do NOT ask the user for input. Make decisions and keep playing.
- Take screenshots at key moments using `mcp__claude-in-chrome__computer` (action: screenshot)
- Check browser console periodically for errors using `mcp__claude-in-chrome__read_console_messages`
- Use `mcp__claude-in-chrome__read_page` and `mcp__claude-in-chrome__find` to locate UI elements
- Use `mcp__claude-in-chrome__form_input` to fill form fields
- Use `mcp__claude-in-chrome__computer` with action: left_click to click buttons and map sectors
- The game canvas is an HTML5 Canvas — coordinates are in pixels. Each sector is 8px wide. You'll need to calculate click positions based on sector coordinates.
- Games with bots start immediately (skip the normal 24-hour scheduling delay).
- **NEVER finish the playthrough with unfiled bugs. Every bug gets an issue or an inline fix.**
