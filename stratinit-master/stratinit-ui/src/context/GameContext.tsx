import { createContext, useContext, useReducer, useCallback, type ReactNode } from 'react'
import type {
  SIUpdate, SIUnitBase, SISector, SIUnit, SICityUpdate, SectorCoords,
  RelationType, CityFieldToUpdate,
} from '../types/game'
import * as gameApi from '../api/game'

// Coordinate key helper
function coordKey(x: number, y: number): string {
  return `${x},${y}`
}

function coordsKey(c: SectorCoords): string {
  return coordKey(c.x, c.y)
}

// Derived lookup maps built from SIUpdate
interface LookupMaps {
  sectorMap: Map<string, SISector>
  unitMap: Map<string, SIUnit[]>
  cityMap: Map<string, SICityUpdate>
}

function buildLookups(update: SIUpdate): LookupMaps {
  const sectorMap = new Map<string, SISector>()
  for (const s of update.sectors) {
    sectorMap.set(coordsKey(s.coords), s)
  }
  const unitMap = new Map<string, SIUnit[]>()
  for (const u of update.units) {
    const key = coordsKey(u.coords)
    const list = unitMap.get(key)
    if (list) list.push(u)
    else unitMap.set(key, [u])
  }
  const cityMap = new Map<string, SICityUpdate>()
  for (const c of update.cities) {
    cityMap.set(coordsKey(c.coords), c)
  }
  return { sectorMap, unitMap, cityMap }
}

// State
interface GameState {
  gameId: number | null
  boardSize: number
  update: SIUpdate | null
  unitBases: SIUnitBase[]
  lookups: LookupMaps | null
  selectedCoords: SectorCoords | null
  selectedUnitIds: Set<number>
  loading: boolean
  error: string | null
}

const initialState: GameState = {
  gameId: null,
  boardSize: 0,
  update: null,
  unitBases: [],
  lookups: null,
  selectedCoords: null,
  selectedUnitIds: new Set(),
  loading: false,
  error: null,
}

// Actions
type GameAction =
  | { type: 'SET_LOADING'; loading: boolean }
  | { type: 'SET_ERROR'; error: string }
  | { type: 'INIT_GAME'; gameId: number; boardSize: number }
  | { type: 'SET_UPDATE'; update: SIUpdate }
  | { type: 'SET_UNIT_BASES'; unitBases: SIUnitBase[] }
  | { type: 'SELECT_SECTOR'; coords: SectorCoords; unitIds: number[] }
  | { type: 'TOGGLE_UNIT'; unitId: number }
  | { type: 'CLEAR_SELECTION' }

function reducer(state: GameState, action: GameAction): GameState {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.loading, error: null }
    case 'SET_ERROR':
      return { ...state, loading: false, error: action.error }
    case 'INIT_GAME':
      return { ...state, gameId: action.gameId, boardSize: action.boardSize }
    case 'SET_UPDATE': {
      const lookups = buildLookups(action.update)
      return { ...state, update: action.update, lookups, loading: false }
    }
    case 'SET_UNIT_BASES':
      return { ...state, unitBases: action.unitBases }
    case 'SELECT_SECTOR':
      return {
        ...state,
        selectedCoords: action.coords,
        selectedUnitIds: new Set(action.unitIds),
      }
    case 'TOGGLE_UNIT': {
      const next = new Set(state.selectedUnitIds)
      if (next.has(action.unitId)) next.delete(action.unitId)
      else next.add(action.unitId)
      return { ...state, selectedUnitIds: next }
    }
    case 'CLEAR_SELECTION':
      return { ...state, selectedCoords: null, selectedUnitIds: new Set() }
    default:
      return state
  }
}

// Context
interface GameContextValue {
  state: GameState
  initGame: (gameId: number, boardSize: number) => Promise<void>
  refreshState: () => Promise<void>
  selectSector: (coords: SectorCoords) => void
  toggleUnit: (unitId: number) => void
  clearSelection: () => void
  moveSelectedUnits: (target: SectorCoords) => Promise<void>
  updateCityProduction: (city: SICityUpdate, field: CityFieldToUpdate, build: string) => Promise<void>
  changeRelation: (nationId: number, relationType: RelationType) => Promise<void>
  getSectorAt: (x: number, y: number) => SISector | undefined
  getUnitsAt: (x: number, y: number) => SIUnit[]
  getCityAt: (x: number, y: number) => SICityUpdate | undefined
  getMyNationId: () => number
}

const GameContext = createContext<GameContextValue | null>(null)

export function GameProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState)

  const refreshState = useCallback(async () => {
    try {
      const result = await gameApi.getUpdate()
      if (result.success) {
        dispatch({ type: 'SET_UPDATE', update: result.value })
      }
    } catch (err) {
      dispatch({ type: 'SET_ERROR', error: 'Failed to fetch game state' })
    }
  }, [])

  const initGame = useCallback(async (gameId: number, boardSize: number) => {
    dispatch({ type: 'SET_LOADING', loading: true })
    dispatch({ type: 'INIT_GAME', gameId, boardSize })
    try {
      await gameApi.setGame(gameId)
      const [updateResult, basesResult] = await Promise.all([
        gameApi.getUpdate(),
        gameApi.getUnitBases(),
      ])
      if (updateResult.success) {
        dispatch({ type: 'SET_UPDATE', update: updateResult.value })
      }
      if (basesResult.success) {
        dispatch({ type: 'SET_UNIT_BASES', unitBases: basesResult.value })
      }
    } catch (err) {
      dispatch({ type: 'SET_ERROR', error: 'Failed to initialize game' })
    }
  }, [])

  const selectSector = useCallback((coords: SectorCoords) => {
    // Auto-select player's units at this sector
    const key = coordKey(coords.x, coords.y)
    const units = state.lookups?.unitMap.get(key) ?? []
    const myNationId = state.update?.nationId ?? -1
    const myUnitIds = units
      .filter(u => u.nationId === myNationId)
      .map(u => u.id)
    dispatch({ type: 'SELECT_SECTOR', coords, unitIds: myUnitIds })
  }, [state.lookups, state.update?.nationId])

  const toggleUnit = useCallback((unitId: number) => {
    dispatch({ type: 'TOGGLE_UNIT', unitId })
  }, [])

  const clearSelection = useCallback(() => {
    dispatch({ type: 'CLEAR_SELECTION' })
  }, [])

  const moveSelectedUnits = useCallback(async (target: SectorCoords) => {
    if (state.selectedUnitIds.size === 0 || !state.update) return
    const allUnits = state.update.units
    const selected = allUnits.filter(u => state.selectedUnitIds.has(u.id))
    if (selected.length === 0) return
    try {
      const result = await gameApi.moveUnits(selected, target)
      if (result.success) {
        dispatch({ type: 'SET_UPDATE', update: result.value })
      }
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch {
      // Silently fail — user can retry
    }
  }, [state.selectedUnitIds, state.update])

  const updateCityProduction = useCallback(async (
    city: SICityUpdate, field: CityFieldToUpdate, build: string,
  ) => {
    const updated: SICityUpdate = {
      ...city,
      field,
      [field === 'BUILD' ? 'build' : 'nextBuild']: build,
    }
    try {
      await gameApi.updateCity(updated)
      await refreshState()
    } catch {
      // Silently fail
    }
  }, [refreshState])

  const changeRelation = useCallback(async (nationId: number, relationType: RelationType) => {
    try {
      await gameApi.setRelation(nationId, relationType)
      await refreshState()
    } catch {
      // Silently fail
    }
  }, [refreshState])

  const getSectorAt = useCallback((x: number, y: number) => {
    return state.lookups?.sectorMap.get(coordKey(x, y))
  }, [state.lookups])

  const getUnitsAt = useCallback((x: number, y: number) => {
    return state.lookups?.unitMap.get(coordKey(x, y)) ?? []
  }, [state.lookups])

  const getCityAt = useCallback((x: number, y: number) => {
    return state.lookups?.cityMap.get(coordKey(x, y))
  }, [state.lookups])

  const getMyNationId = useCallback(() => {
    return state.update?.nationId ?? -1
  }, [state.update?.nationId])

  const value: GameContextValue = {
    state,
    initGame,
    refreshState,
    selectSector,
    toggleUnit,
    clearSelection,
    moveSelectedUnits,
    updateCityProduction,
    changeRelation,
    getSectorAt,
    getUnitsAt,
    getCityAt,
    getMyNationId,
  }

  return <GameContext.Provider value={value}>{children}</GameContext.Provider>
}

export function useGame(): GameContextValue {
  const ctx = useContext(GameContext)
  if (!ctx) throw new Error('useGame must be used within GameProvider')
  return ctx
}
