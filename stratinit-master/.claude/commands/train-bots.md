Train bot AI weights using evolutionary reinforcement learning.

## Instructions

Run the bot RL training simulation to improve `bot-weights.json` weights used by live bot players.

### Step 1: Configure generations

Ask the user how many generations to train. Recommend 30 for a meaningful run (~1 minute), 50+ for thorough training (~2 minutes). Minimum 10.

### Step 2: Run training

Temporarily edit `BotRLTrainingTest.java` to use the requested generation count, then run:

```bash
mvn test -pl stratinit-server -Dtest=BotRLTrainingTest
```

After the test completes, **revert** the test file back to its original 3-generation default.

### Step 3: Extract and display results

Read the training output and display:
- Score history across all generations (show the trajectory)
- The trained best weights from `stratinit-server/training-results/best-weights.json`
- The current production weights from `stratinit-server/src/main/resources/bot-weights.json`
- A comparison table showing each weight: original value, trained value, and the direction/magnitude of change

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
