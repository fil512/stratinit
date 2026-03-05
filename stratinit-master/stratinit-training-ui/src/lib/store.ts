import { writable } from 'svelte/store';
import type {
	TrainingState,
	TrainingEvent,
	TickEvent,
	GameResultEvent,
	GenerationEvent,
	SessionEvent
} from './types';

function createTrainingStore() {
	const initialState: TrainingState = {
		connected: false,
		sessionActive: false,
		totalGenerations: 0,
		ticksPerGame: 0,
		currentGeneration: 0,
		currentGameNum: 0,
		currentTick: 0,
		scoreHistory: [],
		championChanges: [],
		nationSeries: {},
		nationActionTotals: {},
		gameResults: []
	};

	const { subscribe, update } = writable<TrainingState>(initialState);

	function handleEvent(event: TrainingEvent) {
		update((state) => {
			switch (event.type) {
				case 'tick':
					return handleTick(state, event);
				case 'game-result':
					return handleGameResult(state, event);
				case 'generation':
					return handleGeneration(state, event);
				case 'session':
					return handleSession(state, event);
				default:
					return state;
			}
		});
	}

	const ATTACK_ACTIONS = ['AttackEnemyAction', 'AttackNavalAction', 'AttackWithAirAction'];
	const CAPTURE_ACTIONS = ['CaptureNeutralCityAction'];

	function handleTick(state: TrainingState, event: TickEvent): TrainingState {
		const newState = { ...state };

		// Reset nation series on new game
		if (event.gameNum !== state.currentGameNum || event.generation !== state.currentGeneration) {
			newState.nationSeries = {};
			newState.nationActionTotals = {};
			newState.currentGameNum = event.gameNum;
			newState.currentGeneration = event.generation;
		}

		newState.currentTick = event.tick;

		// Accumulate action counts
		const totals = { ...newState.nationActionTotals };
		if (!totals[event.nation]) {
			totals[event.nation] = { attacks: 0, captures: 0 };
		}
		const nationTotal = { ...totals[event.nation] };
		if (event.actions) {
			for (const action of ATTACK_ACTIONS) {
				nationTotal.attacks += event.actions[action] || 0;
			}
			for (const action of CAPTURE_ACTIONS) {
				nationTotal.captures += event.actions[action] || 0;
			}
		}
		totals[event.nation] = nationTotal;
		newState.nationActionTotals = totals;

		const snapshot = {
			tick: event.tick,
			cities: event.cities,
			units: event.units,
			explored: event.explored,
			tech: event.tech,
			attacks: nationTotal.attacks,
			captures: nationTotal.captures
		};

		const series = { ...newState.nationSeries };
		if (!series[event.nation]) {
			series[event.nation] = [];
		}
		series[event.nation] = [...series[event.nation], snapshot];
		newState.nationSeries = series;

		return newState;
	}

	function handleGameResult(state: TrainingState, event: GameResultEvent): TrainingState {
		return {
			...state,
			gameResults: [...state.gameResults, event]
		};
	}

	function handleGeneration(state: TrainingState, event: GenerationEvent): TrainingState {
		return {
			...state,
			currentGeneration: event.generation,
			totalGenerations: event.totalGenerations,
			scoreHistory: event.scoreHistory,
			championChanges: event.championChanged
				? [...state.championChanges, event.generation]
				: state.championChanges,
			gameResults: [] // Reset for next generation
		};
	}

	function handleSession(state: TrainingState, event: SessionEvent): TrainingState {
		if (event.status === 'started') {
			return {
				...initialState,
				connected: true,
				sessionActive: true,
				totalGenerations: event.generations,
				ticksPerGame: event.ticksPerGame
			};
		}
		return { ...state, sessionActive: false };
	}

	return {
		subscribe,
		handleEvent,
		setConnected: (connected: boolean) => update((s) => ({ ...s, connected }))
	};
}

export const trainingStore = createTrainingStore();
