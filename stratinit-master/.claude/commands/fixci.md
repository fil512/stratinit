# Fix CI

Check the most recent GitHub Actions run for this project and fix any failures.

## Step 1: Check CI Status

Run `gh run list --limit 1` to see the most recent workflow run status.

If it succeeded, tell me "CI is green — nothing to fix!" and stop.

If it failed, continue to Step 2.

## Step 2: Get Failure Details

Run `gh run view <run-id> --log-failed` to see the failed job logs.

The CI pipeline has 2 independent jobs. Identify which job(s) failed:

### Job: `build-and-test` (Build & Test Java)
This job runs the full Maven build with tests:
- `mvn clean install -B`

**Fix build failures:** Read the compiler errors from the logs. Common causes:
- Type mismatches after changing DTOs without updating consumers
- Missing imports after moving or renaming classes
- QueryDSL Q-class issues after entity changes (may need `mvn clean install -DskipTests` first)
- Spring wiring issues (missing beans, circular dependencies)

**Fix test failures:** Run `mvn test -pl <module> -Dtest=<TestClass>` locally to reproduce. Common causes:
- H2 SQL compatibility issues (H2 uses `MODE=LEGACY`)
- Liquibase migration conflicts
- Game logic regressions — fix the service/engine code, not the tests
- DataCache consistency issues

### Job: `frontend-checks` (Frontend Checks TypeScript)
This job runs two checks in order:
1. `npx tsc --noEmit` — TypeScript type checking
2. `npm run build` — Vite production build

**Fix TypeScript failures:** Read the tsc errors, fix the types in `stratinit-ui/src/`. Common causes:
- Type mismatches after changing Java DTOs without updating `src/types/game.ts`
- Missing imports after moving or renaming components
- Unused variables or imports

**Fix build failures:** Read the Vite errors, fix the code.

## Step 3: Fix and Validate

1. Make the fix
2. Run the same check locally to confirm it passes
3. Run `mvn clean install` to make sure nothing else broke on the backend
4. Run `cd stratinit-ui && npx tsc --noEmit && npm run build` to verify the frontend
5. Commit and push
6. Run `gh run list --limit 1 --watch` to confirm CI passes
