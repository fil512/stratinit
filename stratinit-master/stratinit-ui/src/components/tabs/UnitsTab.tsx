import { useState, useEffect } from 'react'
import { useGame } from '../../context/GameContext'
import type { SIUnit } from '../../types/game'
import UnitControls from './UnitControls'
import { shrinkTime, formatCountdownShort } from '../../utils/time'

type GroupBy = 'sector' | 'type'

function MobilityProgress({ lastUpdated, blitz }: { lastUpdated: string | null; blitz: boolean }) {
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])

  if (!lastUpdated) return null

  const period = shrinkTime(blitz, 4 * 3600_000)
  const lastUpdatedMs = new Date(lastUpdated).getTime()
  const elapsed = now - lastUpdatedMs
  const elapsedInPeriod = elapsed % period
  const remaining = period - elapsedInPeriod
  const progress = Math.min(elapsedInPeriod / period, 1)

  return (
    <div className="flex items-center gap-1 ml-1 min-w-0">
      <div className="w-16 h-1.5 bg-gray-700 rounded-full overflow-hidden flex-shrink-0">
        <div
          className="h-full bg-green-500 rounded-full transition-[width] duration-1000 ease-linear"
          style={{ width: `${progress * 100}%` }}
        />
      </div>
      <span className="text-[10px] text-gray-400 font-mono tabular-nums flex-shrink-0">
        {formatCountdownShort(remaining)}
      </span>
    </div>
  )
}

function UnitRow({ u, selected, onToggle, requiresFuel, blitz }: { u: SIUnit; selected: boolean; onToggle: (e: React.MouseEvent) => void; requiresFuel: boolean; blitz: boolean }) {
  return (
    <div
      data-testid={`unit-row-${u.id}`}
      onClick={onToggle}
      className={`p-1 rounded cursor-pointer text-xs ${
        selected
          ? 'bg-blue-900 border border-blue-500'
          : 'bg-gray-800 hover:bg-gray-700'
      }`}
    >
      <div className="flex items-center">
        <span className="font-semibold">{u.type}</span>
        <MobilityProgress lastUpdated={u.lastUpdated} blitz={blitz} />
      </div>
      <div>
        HP: {u.hp} | Mob: {u.mobility} | Ammo: {u.ammo}{requiresFuel ? ` | Fuel: ${u.fuel}` : ''}
      </div>
      {u.nextCoords && (
        <div className="text-yellow-400">
          Moving to ({u.nextCoords.x}, {u.nextCoords.y})
        </div>
      )}
    </div>
  )
}

function UnitRowCompact({ u, selected, onToggle, showCoords, requiresFuel, blitz }: {
  u: SIUnit; selected: boolean; onToggle: (e: React.MouseEvent) => void; showCoords?: boolean; requiresFuel: boolean; blitz: boolean
}) {
  return (
    <div
      data-testid={`unit-row-${u.id}`}
      onClick={onToggle}
      className={`p-1 rounded cursor-pointer text-xs ${
        selected
          ? 'bg-blue-900 border border-blue-500'
          : 'bg-gray-800 hover:bg-gray-700'
      }`}
    >
      <div className="flex items-center flex-wrap">
        <span className="font-semibold">{u.type}</span>
        {showCoords && <span className="text-gray-500 ml-1">({u.coords.x},{u.coords.y})</span>}
        <span className="text-gray-400 ml-1">
          HP:{u.hp} Mob:{u.mobility} A:{u.ammo}{requiresFuel ? ` F:${u.fuel}` : ''}
        </span>
        <MobilityProgress lastUpdated={u.lastUpdated} blitz={blitz} />
        {u.nextCoords && (
          <span className="text-yellow-400 ml-1">
            → ({u.nextCoords.x},{u.nextCoords.y})
          </span>
        )}
      </div>
    </div>
  )
}

export default function UnitsTab() {
  const { state, toggleUnit, selectOnlyUnit, selectSector } = useGame()
  const { selectedCoords, selectedUnitIds, update, unitBases } = state
  const [groupBy, setGroupBy] = useState<GroupBy>('sector')

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myNationId = update.nationId
  const blitz = update.blitz ?? false
  const allMyUnits = update.units.filter(u => u.nationId === myNationId)

  if (allMyUnits.length === 0) {
    return <p className="text-gray-500">No units.</p>
  }

  function handleToggle(u: SIUnit, e: React.MouseEvent) {
    // Select the sector first if not already selected
    if (!selectedCoords || selectedCoords.x !== u.coords.x || selectedCoords.y !== u.coords.y) {
      selectSector(u.coords)
    }
    if (e.shiftKey) {
      toggleUnit(u.id)
    } else {
      selectOnlyUnit(u.id)
    }
  }

  function handleSelectSector(x: number, y: number) {
    selectSector({ x, y })
  }

  const groupedBySector = () => {
    const map = new Map<string, SIUnit[]>()
    for (const u of allMyUnits) {
      const key = `${u.coords.x},${u.coords.y}`
      const list = map.get(key)
      if (list) list.push(u)
      else map.set(key, [u])
    }
    return [...map.entries()].sort((a, b) => a[0].localeCompare(b[0]))
  }

  const groupedByType = () => {
    const map = new Map<string, SIUnit[]>()
    for (const u of allMyUnits) {
      const list = map.get(u.type)
      if (list) list.push(u)
      else map.set(u.type, [u])
    }
    return [...map.entries()].sort((a, b) => a[0].localeCompare(b[0]))
  }

  return (
    <div data-testid="units-list" className="space-y-2">
      <div className="flex justify-between items-center">
        <h3 className="font-bold text-gray-300">Units ({allMyUnits.length})</h3>
        <div className="flex gap-1">
          <button
            onClick={() => setGroupBy('sector')}
            className={`text-xs px-2 py-0.5 rounded ${
              groupBy === 'sector' ? 'bg-blue-700 text-white' : 'bg-gray-700 hover:bg-gray-600'
            }`}
          >
            Sector
          </button>
          <button
            onClick={() => setGroupBy('type')}
            className={`text-xs px-2 py-0.5 rounded ${
              groupBy === 'type' ? 'bg-blue-700 text-white' : 'bg-gray-700 hover:bg-gray-600'
            }`}
          >
            Type
          </button>
        </div>
      </div>

      <UnitControls />

      {groupBy === 'sector' && groupedBySector().map(([key, units]) => {
        const [x, y] = key.split(',').map(Number)
        const isSelected = selectedCoords?.x === x && selectedCoords?.y === y
        return (
          <div key={key}>
            <div
              className={`text-xs cursor-pointer hover:underline mb-0.5 ${
                isSelected ? 'text-blue-300 font-semibold' : 'text-blue-400'
              }`}
              onClick={() => handleSelectSector(x, y)}
            >
              ({x}, {y}) — {units.length} unit{units.length !== 1 ? 's' : ''}
            </div>
            {units.map(u => (
              <UnitRow
                key={u.id}
                u={u}
                selected={selectedUnitIds.has(u.id)}
                onToggle={(e) => handleToggle(u, e)}
                requiresFuel={unitBases.find(ub => ub.type === u.type)?.requiresFuel ?? false}
                blitz={blitz}
              />
            ))}
          </div>
        )
      })}

      {groupBy === 'type' && groupedByType().map(([type, units]) => (
        <div key={type}>
          <div className="text-xs text-gray-300 font-semibold mb-0.5">
            {type} ({units.length})
          </div>
          {units.map(u => (
            <UnitRowCompact
              key={u.id}
              u={u}
              selected={selectedUnitIds.has(u.id)}
              onToggle={(e) => handleToggle(u, e)}
              showCoords
              requiresFuel={unitBases.find(ub => ub.type === u.type)?.requiresFuel ?? false}
              blitz={blitz}
            />
          ))}
        </div>
      ))}
    </div>
  )
}
