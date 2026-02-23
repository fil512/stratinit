# Fix All Failing Tests

Automatically find and fix all failing tests in the project until the full build passes.

## Procedure

1. Run `mvn clean install` to identify all failing tests
2. If the build succeeds, tell the user "All tests pass — nothing to fix!" and stop
3. Parse the test failures from Maven's surefire output to identify:
   - Which module(s) have failures
   - Which test class(es) and method(s) are failing
   - The root cause from the error messages/stack traces
4. Fix the **first failing test group** (all failures in one test class):
   - Read the failing test to understand what it expects
   - Read the source code being tested
   - Fix the source code (not the test) unless the test itself is clearly wrong
   - Run `mvn test -pl <module> -Dtest=<TestClass>` to confirm the fix
5. After fixing one group, go back to Step 1 to find the next failure
6. Repeat until `mvn clean install` passes completely
7. Commit all fixes together:
   ```bash
   git add <files>
   git commit -m "fix: resolve failing tests across modules"
   git push
   ```
8. Summarize all test groups that were fixed (list modules, test classes, and root causes)
