<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { Chart, registerables } from 'chart.js';
	import type { NationSnapshot } from '$lib/types';

	Chart.register(...registerables);

	export let nationSeries: Record<string, NationSnapshot[]> = {};
	export let metric: 'cities' | 'units' | 'explored' | 'tech' | 'nationsFound' | 'icbmsLaunched' | 'attacks' | 'captures' = 'cities';
	export let title: string = 'Cities';

	const COLORS = [
		'#3b82f6', '#ef4444', '#10b981', '#f59e0b',
		'#8b5cf6', '#ec4899', '#06b6d4', '#f97316'
	];

	let canvas: HTMLCanvasElement;
	let chart: Chart | null = null;

	$: if (chart) {
		const nations = Object.keys(nationSeries);
		chart.data.datasets = nations.map((nation, i) => ({
			label: nation,
			data: nationSeries[nation].map((s) => ({ x: s.tick, y: s[metric] })),
			borderColor: COLORS[i % COLORS.length],
			borderWidth: 1.5,
			pointRadius: 0,
			tension: 0.3,
			fill: false
		}));
		// Compute tick labels from first nation
		if (nations.length > 0) {
			chart.data.labels = nationSeries[nations[0]].map((s) => s.tick);
		}
		chart.update('none');
	}

	onMount(() => {
		chart = new Chart(canvas, {
			type: 'line',
			data: { labels: [], datasets: [] },
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					title: { display: true, text: title, color: '#e2e8f0' },
					legend: { labels: { color: '#94a3b8', boxWidth: 12, font: { size: 10 } } }
				},
				scales: {
					x: {
						type: 'linear',
						title: { display: true, text: 'Tick', color: '#94a3b8' },
						ticks: { color: '#94a3b8', maxTicksLimit: 10 },
						grid: { color: '#334155' }
					},
					y: {
						ticks: { color: '#94a3b8' },
						grid: { color: '#334155' }
					}
				},
				animation: false
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
		height: 250px;
		position: relative;
	}
</style>
