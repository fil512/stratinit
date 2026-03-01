Train bot AI weights using evolutionary reinforcement learning.

## Instructions

Run the bot RL training simulation to improve `bot-weights.json` weights used by live bot players.

### Step 1: Configure generations

Ask the user how many generations to train. Offer options: 10 (quick test), 20 (short run), 30 (meaningful run ~1 minute), 50 (thorough ~2 minutes), 100 (extended). Minimum 10.

### Step 2: Run training

Run the training with the requested generation count via `-Dtraining.generations=N`:

```bash
mvn test -pl stratinit-server -Dtest=BotRLTrainingTest -Dtraining.generations=30
```

### Step 3: Extract and display results

Read the training output and display:
- Score history across all generations (show the trajectory)
- The trained best weights from `stratinit-server/training-results/best-weights.json`
- The current production weights from `stratinit-server/src/main/resources/bot-weights.json`
- A comparison table showing each weight: original value, trained value, and the direction/magnitude of change

### Step 3.5: Analyze bot behavior

Read `training-results/training-analysis.json` (generated automatically by the training run) and present:
- **Action frequencies**: Which actions bots actually execute, sorted by count. Highlight any action types with zero executions that seem important (e.g., LoadTransportAction, DisembarkUnitAction).
- **Milestone timing**: Average turn number for first transport built, first non-home city capture, first island landing. Flag if milestones are missing entirely.
- **Failure patterns**: List all detected patterns (e.g., "Transport built but never loaded", "Never expanded off home island") with the fraction of nations affected.
- **State progression**: Show the average cities/units/explored/tech at turns 0, 10, 20, ..., 90. Look for plateaus (e.g., cities stuck at 4 after turn 20 = no expansion).

Based on the patterns found, recommend specific **code improvements** to the bot logic (not just weight changes). For example:
- If transports are built but never loaded → investigate `LoadTransportAction` generation or utility scoring
- If exploration is low → check frontier exploration action generation
- If cities plateau early → check transport/disembark pipeline

### Step 4: Evaluate improvement

Compare the trained weights against the baseline:
- Look at the score history trajectory — is it trending upward or flat/noisy?
- Check if the final generation's best score is higher than the first generation's
- Flag if scores are highly volatile (suggesting insufficient games per generation)

Present the analysis and recommend whether the trained weights look like a genuine improvement or just noise.

### Step 5: Update production weights (with confirmation)

Ask the user whether to promote the trained weights to production. If they approve:

1. Copy `stratinit-server/training-results/best-weights.json` content to `stratinit-server/src/main/resources/bot-weights.json`
2. Show the diff of what changed

If they decline, leave production weights unchanged. The training results remain in `stratinit-server/training-results/` for future reference or as a seed for the next training run.
