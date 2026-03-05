<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { Chart, registerables } from 'chart.js';

	Chart.register(...registerables);

	export let scoreHistory: number[] = [];
	export let championChanges: number[] = [];

	let canvas: HTMLCanvasElement;
	let chart: Chart | null = null;

	$: if (chart) {
		chart.data.labels = scoreHistory.map((_, i) => `Gen ${i + 1}`);
		const ds = chart.data.datasets[0] as unknown as Record<string, unknown>;
		ds.data = scoreHistory;
		// Mark champion changes with point styles
		ds.pointBackgroundColor = scoreHistory.map((_, i) =>
			championChanges.includes(i + 1) ? '#ef4444' : '#3b82f6'
		);
		ds.pointRadius = scoreHistory.map((_, i) =>
			championChanges.includes(i + 1) ? 6 : 3
		);
		chart.update('none');
	}

	onMount(() => {
		chart = new Chart(canvas, {
			type: 'line',
			data: {
				labels: [],
				datasets: [
					{
						label: 'Best Score (z-score)',
						data: [],
						borderColor: '#3b82f6',
						backgroundColor: 'rgba(59, 130, 246, 0.1)',
						fill: true,
						tension: 0.3,
						pointBackgroundColor: '#3b82f6',
						pointRadius: 3
					}
				]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					title: { display: true, text: 'Score Trajectory Across Generations', color: '#e2e8f0' },
					legend: { labels: { color: '#e2e8f0' } }
				},
				scales: {
					x: { ticks: { color: '#94a3b8' }, grid: { color: '#334155' } },
					y: { ticks: { color: '#94a3b8' }, grid: { color: '#334155' } }
				}
			}
		});
	});

	onDestroy(() => chart?.destroy());
</script>

<div class="chart-container">
	<canvas bind:this={canvas}></canvas>
</div>

<style>
	.chart-container {
		height: 300px;
		position: relative;
	}
</style>
