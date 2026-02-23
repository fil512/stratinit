# Fix All Issues

Automatically loop through all open issues, fixing each one using TDD until none remain.

## Procedure

1. Run `/fixbug` to fix the next open issue
2. After the fix is pushed, immediately run `/fixbug` again (do NOT ask the user — just continue)
3. Repeat until `/fixbug` reports "No open issues found!"
4. Summarize all issues that were fixed (list issue numbers and titles)
