import { createContext, useContext, useState, useCallback, type ReactNode } from 'react'

export interface PowerBreakdownEntry {
  type: string
  abbrev: string
  count: number
  prodTime: number
  subtotal: number  // count * prodTime
}

interface GameInfo {
  gameId: number
  gameName: string
  gameEnds: string | null
  power: number
  powerLimit: number
  powerBreakdown: PowerBreakdownEntry[]
  cities: number
  commandPoints: number
  hourlyCPGain: number
}

interface TickInfo {
  lastUpdated: string
  tickIntervalMs: number
  game: GameInfo | null
}

interface TickContextValue {
  tickInfo: TickInfo | null
  setTickInfo: (lastUpdated: string | null, tickIntervalMs: number, game?: GameInfo) => void
  clearTickInfo: () => void
}

const TickContext = createContext<TickContextValue | null>(null)

export function TickProvider({ children }: { children: ReactNode }) {
  const [tickInfo, setTickInfoState] = useState<TickInfo | null>(null)

  const setTickInfo = useCallback((lastUpdated: string | null, tickIntervalMs: number, game?: GameInfo) => {
    if (lastUpdated && tickIntervalMs > 0) {
      setTickInfoState({ lastUpdated, tickIntervalMs, game: game ?? null })
    }
  }, [])

  const clearTickInfo = useCallback(() => {
    setTickInfoState(null)
  }, [])

  return (
    <TickContext.Provider value={{ tickInfo, setTickInfo, clearTickInfo }}>
      {children}
    </TickContext.Provider>
  )
}

export function useTick(): TickContextValue {
  const ctx = useContext(TickContext)
  if (!ctx) throw new Error('useTick must be used within TickProvider')
  return ctx
}
