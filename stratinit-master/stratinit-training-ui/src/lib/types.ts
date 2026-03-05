export interface TickEvent {
	type: 'tick';
	generation: number;
	gameNum: number;
	tick: number;
	nation: string;
	cities: number;
	units: number;
	explored: number;
	tech: number;
	hasTransport: boolean;
	actions?: Record<string, number>;
}

export interface GameResultEvent {
	type: 'game-result';
	generation: number;
	gameNum: number;
	turnsPlayed: number;
	scores: Record<string, number>;
	milestones?: Record<string, Record<string, number>>;
}

export interface GenerationEvent {
	type: 'generation';
	generation: number;
	totalGenerations: number;
	championChanged: boolean;
	championScore: number;
	bestChallengerScore: number;
	scoreHistory: number[];
}

export interface SessionEvent {
	type: 'session';
	status: 'started' | 'completed';
	generations: number;
	ticksPerGame: number;
	timestamp: number;
}

export type TrainingEvent = TickEvent | GameResultEvent | GenerationEvent | SessionEvent;

export interface NationSnapshot {
	tick: number;
	cities: number;
	units: number;
	explored: number;
	tech: number;
}

export interface TrainingState {
	connected: boolean;
	sessionActive: boolean;
	totalGenerations: number;
	ticksPerGame: number;
	currentGeneration: number;
	currentGameNum: number;
	currentTick: number;
	scoreHistory: number[];
	championChanges: number[];
	// Per-nation time series for current game
	nationSeries: Record<string, NationSnapshot[]>;
	// Game results for current generation
	gameResults: GameResultEvent[];
}
