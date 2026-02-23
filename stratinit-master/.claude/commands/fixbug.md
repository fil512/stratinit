# Fix Next Issue (TDD)

Fix the next open issue from GitHub Issues using test-driven development.

## Step 1: Find the Next Issue

Run `gh issue list --state open --json number,title,body | jq 'sort_by(.number) | .[0]'` to get the oldest open issue (lowest issue number). Note: `gh issue list` returns newest first by default, so sorting is required.

If no open issues exist, tell the user "No open issues found!" and stop.

Display the issue title and number to the user.

## Step 2: Understand the Bug

Read the issue body to understand:

- What's broken
- What the expected behavior is
- Which files are affected

Read the affected source files to understand the current implementation. Key source locations:
- Domain services: `stratinit-server/src/main/java/.../server/service/`
- REST services: `stratinit-rest/src/main/java/.../rest/svc/`
- Controllers: `stratinit-rest/src/main/java/.../rest/controller/`
- Entities: `stratinit-core/src/main/java/.../client/model/`
- DTOs: `stratinit-core/src/main/java/.../client/model/` (prefixed with `SI`)
- React UI: `stratinit-ui/src/`
- Unit definitions: `stratinit-core/src/main/resources/unit-definitions.json`
- Bot AI: `stratinit-server/src/main/java/.../server/bot/`

## Step 3: Write a Failing Test First (TDD Red Phase)

Write a test that demonstrates the issue. The test should:

- Be in the appropriate test file in the relevant module's `src/test/java/` directory
- Use the test helpers from `stratinit-test` (`PlayerHelper`, `GameHelper`, `NationHelper`, `UnitHelper`, `WorldHelper`, `SectorHelper`)
- Extend `StratInitDaoBase` for database-backed tests
- Clearly describe what it's testing in the test name (reference the issue number)
- FAIL with the current code (proving the issue exists)

Run the test to confirm it fails:

```bash
mvn test -pl <module> -Dtest=<TestClass>#<methodName>
```

**For UI-only bugs** (e.g., issues in React components) where a Java unit test isn't practical, skip to Step 4 and note in the commit that the fix is verified by manual testing.

## Step 4: Fix the Bug (TDD Green Phase)

Make the minimal code change to fix the issue. Follow the existing patterns:

1. Fix the source code
2. Run the test again to confirm it passes
3. Keep the fix focused — don't refactor surrounding code

## Step 5: Verify

Run the full test suite for the affected module(s) to make sure nothing else broke:

```bash
mvn test -pl <module>
```

For cross-cutting changes, run the full build:

```bash
mvn clean install
```

## Step 6: Commit and Close

Commit the fix on the current branch:

```bash
git add <files>
git commit -m "fix: <description> (closes #<issue-number>)"
git push
```

The `closes #N` syntax auto-closes the GitHub issue when pushed.

## Step 7: Next Bug

After the fix is pushed, ask the user: "Issue #N fixed. Fix the next issue?"

- If yes, go back to Step 1
- If no, summarize what was fixed
