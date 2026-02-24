import { createContext, useContext, useState, useCallback, type ReactNode } from 'react'

interface TickInfo {
  lastUpdated: string
  tickIntervalMs: number
}

interface TickContextValue {
  tickInfo: TickInfo | null
  setTickInfo: (lastUpdated: string | null, tickIntervalMs: number) => void
  clearTickInfo: () => void
}

const TickContext = createContext<TickContextValue | null>(null)

export function TickProvider({ children }: { children: ReactNode }) {
  const [tickInfo, setTickInfoState] = useState<TickInfo | null>(null)

  const setTickInfo = useCallback((lastUpdated: string | null, tickIntervalMs: number) => {
    if (lastUpdated && tickIntervalMs > 0) {
      setTickInfoState({ lastUpdated, tickIntervalMs })
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
