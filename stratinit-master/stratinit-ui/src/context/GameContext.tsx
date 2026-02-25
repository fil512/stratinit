import { createContext, useContext, useReducer, useCallback, useEffect, type ReactNode } from 'react'
import type {
  SIUpdate, SIUnitBase, SISector, SIUnit, SICityUpdate, SectorCoords,
  RelationType, CityFieldToUpdate,
} from '../types/game'
import * as gameApi from '../api/game'
import { useTick } from './TickContext'

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
  commandError: string | null
  sectorSelectedAt: number
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
  commandError: null,
  sectorSelectedAt: 0,
}

// Actions
type GameAction =
  | { type: 'SET_LOADING'; loading: boolean }
  | { type: 'SET_ERROR'; error: string }
  | { type: 'INIT_GAME'; gameId: number; boardSize: number }
  | { type: 'SET_UPDATE'; update: SIUpdate }
  | { type: 'SET_UNIT_BASES'; unitBases: SIUnitBase[] }
  | { type: 'SELECT_SECTOR'; coords: SectorCoords; unitIds: number[]; fromMap?: boolean }
  | { type: 'TOGGLE_UNIT'; unitId: number }
  | { type: 'SELECT_ONLY_UNIT'; unitId: number }
  | { type: 'CLEAR_SELECTION' }
  | { type: 'SET_COMMAND_ERROR'; error: string }
  | { type: 'CLEAR_COMMAND_ERROR' }

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
        sectorSelectedAt: action.fromMap ? Date.now() : state.sectorSelectedAt,
      }
    case 'TOGGLE_UNIT': {
      const next = new Set(state.selectedUnitIds)
      if (next.has(action.unitId)) next.delete(action.unitId)
      else next.add(action.unitId)
      return { ...state, selectedUnitIds: next }
    }
    case 'SELECT_ONLY_UNIT':
      return { ...state, selectedUnitIds: new Set([action.unitId]) }
    case 'CLEAR_SELECTION':
      return { ...state, selectedCoords: null, selectedUnitIds: new Set() }
    case 'SET_COMMAND_ERROR':
      return { ...state, commandError: action.error }
    case 'CLEAR_COMMAND_ERROR':
      return { ...state, commandError: null }
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
  selectSectorFromMap: (coords: SectorCoords) => void
  toggleUnit: (unitId: number) => void
  selectOnlyUnit: (unitId: number) => void
  clearSelection: () => void
  moveSelectedUnits: (target: SectorCoords) => Promise<void>
  disbandSelectedUnits: () => Promise<void>
  cancelSelectedMoves: () => Promise<void>
  buildCityWithUnit: () => Promise<void>
  switchTerrainWithUnit: () => Promise<void>
  cedeSelectedUnits: (nationId: number) => Promise<void>
  cedeCityAt: (city: SICityUpdate, nationId: number) => Promise<void>
  updateCityProduction: (city: SICityUpdate, field: CityFieldToUpdate, build: string) => Promise<void>
  changeRelation: (nationId: number, relationType: RelationType) => Promise<void>
  concedeGame: () => Promise<void>
  commandError: string | null
  getSectorAt: (x: number, y: number) => SISector | undefined
  getUnitsAt: (x: number, y: number) => SIUnit[]
  getCityAt: (x: number, y: number) => SICityUpdate | undefined
  getMyNationId: () => number
}

const GameContext = createContext<GameContextValue | null>(null)

export function GameProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const { setTickInfo, clearTickInfo } = useTick()

  const dispatchUpdate = useCallback((update: SIUpdate) => {
    dispatch({ type: 'SET_UPDATE', update })
    setTickInfo(update.lastUpdated, update.tickIntervalMs, {
      gameId: update.gameId,
      gameName: update.gameName,
      gameEnds: update.gameEnds,
    })
  }, [setTickInfo])

  useEffect(() => {
    return () => clearTickInfo()
  }, [clearTickInfo])

  const refreshState = useCallback(async () => {
    try {
      const update = await gameApi.getUpdate()
      dispatchUpdate(update)
    } catch (err) {
      dispatch({ type: 'SET_ERROR', error: 'Failed to fetch game state' })
    }
  }, [dispatchUpdate])

  const initGame = useCallback(async (gameId: number, boardSize: number) => {
    dispatch({ type: 'SET_LOADING', loading: true })
    dispatch({ type: 'INIT_GAME', gameId, boardSize })
    try {
      await gameApi.setGame(gameId)
      const [update, unitBases] = await Promise.all([
        gameApi.getUpdate(),
        gameApi.getUnitBases(),
      ])
      dispatchUpdate(update)
      dispatch({ type: 'SET_UNIT_BASES', unitBases })
    } catch (err) {
      dispatch({ type: 'SET_ERROR', error: 'Failed to initialize game' })
    }
  }, [dispatchUpdate])

  const selectSectorImpl = useCallback((coords: SectorCoords, fromMap: boolean) => {
    const key = coordKey(coords.x, coords.y)
    const units = state.lookups?.unitMap.get(key) ?? []
    const myNationId = state.update?.nationId ?? -1
    const myUnitIds = units
      .filter(u => u.nationId === myNationId)
      .map(u => u.id)
    dispatch({ type: 'SELECT_SECTOR', coords, unitIds: myUnitIds, fromMap })
  }, [state.lookups, state.update?.nationId])

  const selectSector = useCallback((coords: SectorCoords) => {
    selectSectorImpl(coords, false)
  }, [selectSectorImpl])

  const selectSectorFromMap = useCallback((coords: SectorCoords) => {
    selectSectorImpl(coords, true)
  }, [selectSectorImpl])

  const toggleUnit = useCallback((unitId: number) => {
    dispatch({ type: 'TOGGLE_UNIT', unitId })
  }, [])

  const selectOnlyUnit = useCallback((unitId: number) => {
    dispatch({ type: 'SELECT_ONLY_UNIT', unitId })
  }, [])

  const clearSelection = useCallback(() => {
    dispatch({ type: 'CLEAR_SELECTION' })
  }, [])

  const showCommandError = useCallback((msg: string) => {
    dispatch({ type: 'SET_COMMAND_ERROR', error: msg })
    setTimeout(() => dispatch({ type: 'CLEAR_COMMAND_ERROR' }), 4000)
  }, [])

  const moveSelectedUnits = useCallback(async (target: SectorCoords) => {
    if (state.selectedUnitIds.size === 0 || !state.update) return
    const allUnits = state.update.units
    const selected = allUnits.filter(u => state.selectedUnitIds.has(u.id))
    if (selected.length === 0) return
    try {
      const update = await gameApi.moveUnits(selected, target)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
      if (update.messages && update.messages.length > 0) {
        showCommandError(update.messages.join('; '))
      }
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Move failed')
    }
  }, [state.selectedUnitIds, state.update, showCommandError])

  const getSelectedUnits = useCallback(() => {
    if (state.selectedUnitIds.size === 0 || !state.update) return []
    return state.update.units.filter(u => state.selectedUnitIds.has(u.id))
  }, [state.selectedUnitIds, state.update])

  const disbandSelectedUnits = useCallback(async () => {
    const selected = getSelectedUnits()
    if (selected.length === 0) return
    try {
      const update = await gameApi.disbandUnits(selected)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Disband failed')
    }
  }, [getSelectedUnits, showCommandError])

  const cancelSelectedMoves = useCallback(async () => {
    const selected = getSelectedUnits().filter(u => u.nextCoords !== null)
    if (selected.length === 0) return
    try {
      const update = await gameApi.cancelMove(selected)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Cancel move failed')
    }
  }, [getSelectedUnits, showCommandError])

  const buildCityWithUnit = useCallback(async () => {
    const selected = getSelectedUnits().filter(u => u.type === 'ENGINEER')
    if (selected.length === 0) return
    try {
      const update = await gameApi.buildCity(selected)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Build city failed')
    }
  }, [getSelectedUnits, showCommandError])

  const switchTerrainWithUnit = useCallback(async () => {
    const selected = getSelectedUnits().filter(u => u.type === 'ENGINEER')
    if (selected.length === 0) return
    try {
      const update = await gameApi.switchTerrain(selected)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Switch terrain failed')
    }
  }, [getSelectedUnits, showCommandError])

  const cedeSelectedUnits = useCallback(async (nationId: number) => {
    const selected = getSelectedUnits()
    if (selected.length === 0) return
    try {
      const update = await gameApi.cedeUnits(selected, nationId)
      dispatchUpdate(update)
      dispatch({ type: 'CLEAR_SELECTION' })
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Cede units failed')
    }
  }, [getSelectedUnits, showCommandError])

  const cedeCityAt = useCallback(async (city: SICityUpdate, nationId: number) => {
    try {
      const update = await gameApi.cedeCity(city, nationId)
      dispatchUpdate(update)
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Cede city failed')
    }
  }, [showCommandError])

  const updateCityProduction = useCallback(async (
    city: SICityUpdate, field: CityFieldToUpdate, build: string,
  ) => {
    const updated: SICityUpdate = {
      ...city,
      field,
      [field === 'BUILD' ? 'build' : 'nextBuild']: build || null,
    }
    try {
      await gameApi.updateCity(updated)
      await refreshState()
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Update city failed')
    }
  }, [refreshState, showCommandError])

  const changeRelation = useCallback(async (nationId: number, relationType: RelationType) => {
    try {
      await gameApi.setRelation(nationId, relationType)
      await refreshState()
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Set relation failed')
    }
  }, [refreshState, showCommandError])

  const concedeGame = useCallback(async () => {
    try {
      const update = await gameApi.concede()
      dispatchUpdate(update)
    } catch (err) {
      showCommandError(err instanceof Error ? err.message : 'Concede failed')
    }
  }, [showCommandError])

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
    selectSectorFromMap,
    toggleUnit,
    selectOnlyUnit,
    clearSelection,
    moveSelectedUnits,
    disbandSelectedUnits,
    cancelSelectedMoves,
    buildCityWithUnit,
    switchTerrainWithUnit,
    cedeSelectedUnits,
    cedeCityAt,
    updateCityProduction,
    changeRelation,
    concedeGame,
    commandError: state.commandError,
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
