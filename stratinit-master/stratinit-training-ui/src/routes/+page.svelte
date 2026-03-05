<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { trainingStore } from '$lib/store';
	import type { TrainingEvent } from '$lib/types';
	import ScoreChart from '$lib/components/ScoreChart.svelte';
	import GameChart from '$lib/components/GameChart.svelte';

	let eventSource: EventSource | null = null;

	onMount(() => {
		eventSource = new EventSource('/api/events');

		eventSource.onopen = () => {
			trainingStore.setConnected(true);
		};

		eventSource.onmessage = (event) => {
			try {
				const data = JSON.parse(event.data) as TrainingEvent | { type: string };
				if (data.type === 'connected' || data.type === 'error') {
					trainingStore.setConnected(data.type === 'connected');
					return;
				}
				trainingStore.handleEvent(data as TrainingEvent);
			} catch {
				// Ignore parse errors (heartbeats, etc.)
			}
		};

		eventSource.onerror = () => {
			trainingStore.setConnected(false);
		};
	});

	onDestroy(() => {
		eventSource?.close();
	});

	$: state = $trainingStore;
	$: nationCount = Object.keys(state.nationSeries).length;
	$: progress =
		state.totalGenerations > 0
			? ((state.currentGeneration - 1) / state.totalGenerations) * 100
			: 0;
	$: gameProgress =
		state.ticksPerGame > 0 ? (state.currentTick / state.ticksPerGame) * 100 : 0;
</script>

<div class="dashboard">
	<header>
		<h1>Training Dashboard</h1>
		<div class="status">
			<span class="dot" class:connected={state.connected} class:active={state.sessionActive}></span>
			{#if state.sessionActive}
				Training in progress
			{:else if state.connected}
				Connected — waiting for training
			{:else}
				Connecting to Redis...
			{/if}
		</div>
	</header>

	{#if state.sessionActive || state.scoreHistory.length > 0}
		<section class="progress-section">
			<div class="progress-bar-group">
				<label>
					Generation {state.currentGeneration}/{state.totalGenerations}
				</label>
				<div class="progress-bar">
					<div class="progress-fill" style="width: {progress}%"></div>
				</div>
			</div>
			<div class="progress-bar-group">
				<label>
					Game {state.currentGameNum} — Tick {state.currentTick}/{state.ticksPerGame}
				</label>
				<div class="progress-bar">
					<div class="progress-fill game" style="width: {gameProgress}%"></div>
				</div>
			</div>
		</section>

		<section class="charts">
			<div class="chart-card full">
				<ScoreChart
					scoreHistory={state.scoreHistory}
					championChanges={state.championChanges}
				/>
			</div>

			{#if nationCount > 0}
				<div class="chart-card">
					<GameChart
						nationSeries={state.nationSeries}
						metric="cities"
						title="Cities per Nation"
					/>
				</div>
				<div class="chart-card">
					<GameChart
						nationSeries={state.nationSeries}
						metric="units"
						title="Units per Nation"
					/>
				</div>
				<div class="chart-card">
					<GameChart
						nationSeries={state.nationSeries}
						metric="explored"
						title="Explored Sectors"
					/>
				</div>
				<div class="chart-card">
					<GameChart
						nationSeries={state.nationSeries}
						metric="tech"
						title="Tech Level"
					/>
				</div>
			{/if}
		</section>

		{#if state.gameResults.length > 0}
			<section class="results">
				<h2>Game Results (Generation {state.currentGeneration})</h2>
				<div class="results-grid">
					{#each state.gameResults as result}
						<div class="result-card">
							<h3>Game {result.gameNum}</h3>
							<p>{result.turnsPlayed} turns</p>
							<table>
								<thead>
									<tr><th>Nation</th><th>Score</th></tr>
								</thead>
								<tbody>
									{#each Object.entries(result.scores).sort((a, b) => b[1] - a[1]) as [name, score]}
										<tr>
											<td>{name}</td>
											<td class:positive={score > 0} class:negative={score < 0}>
												{score.toFixed(3)}
											</td>
										</tr>
									{/each}
								</tbody>
							</table>
						</div>
					{/each}
				</div>
			</section>
		{/if}
	{:else}
		<section class="empty">
			<p>No training data yet. Start a training run:</p>
			<code>mvn test -pl stratinit-server -Dtest=BotRLTrainingTest -Dtraining.generations=10 -Dtraining.ticks=960</code>
		</section>
	{/if}
</div>

<style>
	:global(body) {
		margin: 0;
		font-family:
			-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
		background: #0f172a;
		color: #e2e8f0;
	}

	.dashboard {
		max-width: 1200px;
		margin: 0 auto;
		padding: 1rem;
	}

	header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 1.5rem;
		padding-bottom: 1rem;
		border-bottom: 1px solid #334155;
	}

	h1 {
		margin: 0;
		font-size: 1.5rem;
		color: #f1f5f9;
	}

	.status {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-size: 0.875rem;
		color: #94a3b8;
	}

	.dot {
		width: 8px;
		height: 8px;
		border-radius: 50%;
		background: #64748b;
	}

	.dot.connected {
		background: #facc15;
	}

	.dot.active {
		background: #22c55e;
		animation: pulse 2s infinite;
	}

	@keyframes pulse {
		0%,
		100% {
			opacity: 1;
		}
		50% {
			opacity: 0.5;
		}
	}

	.progress-section {
		display: flex;
		gap: 1.5rem;
		margin-bottom: 1.5rem;
	}

	.progress-bar-group {
		flex: 1;
	}

	.progress-bar-group label {
		display: block;
		font-size: 0.75rem;
		color: #94a3b8;
		margin-bottom: 0.25rem;
	}

	.progress-bar {
		height: 6px;
		background: #1e293b;
		border-radius: 3px;
		overflow: hidden;
	}

	.progress-fill {
		height: 100%;
		background: #3b82f6;
		border-radius: 3px;
		transition: width 0.3s ease;
	}

	.progress-fill.game {
		background: #10b981;
	}

	.charts {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 1rem;
		margin-bottom: 1.5rem;
	}

	.chart-card {
		background: #1e293b;
		border-radius: 8px;
		padding: 1rem;
	}

	.chart-card.full {
		grid-column: 1 / -1;
	}

	.results h2 {
		font-size: 1.1rem;
		margin: 0 0 1rem;
	}

	.results-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
		gap: 1rem;
	}

	.result-card {
		background: #1e293b;
		border-radius: 8px;
		padding: 1rem;
	}

	.result-card h3 {
		margin: 0 0 0.25rem;
		font-size: 0.9rem;
	}

	.result-card p {
		margin: 0 0 0.5rem;
		font-size: 0.75rem;
		color: #94a3b8;
	}

	table {
		width: 100%;
		border-collapse: collapse;
		font-size: 0.8rem;
	}

	th {
		text-align: left;
		padding: 0.25rem 0.5rem;
		color: #94a3b8;
		border-bottom: 1px solid #334155;
	}

	td {
		padding: 0.2rem 0.5rem;
	}

	.positive {
		color: #22c55e;
	}

	.negative {
		color: #ef4444;
	}

	.empty {
		text-align: center;
		padding: 3rem 1rem;
		color: #94a3b8;
	}

	.empty code {
		display: block;
		margin-top: 1rem;
		padding: 0.75rem 1rem;
		background: #1e293b;
		border-radius: 6px;
		font-size: 0.8rem;
		color: #e2e8f0;
		word-break: break-all;
	}
</style>
