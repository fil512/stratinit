import { useState } from 'react'
import { useGame } from '../../context/GameContext'
import type { SIUnit } from '../../types/game'
import UnitControls from './UnitControls'
import { unitIconPath } from '../../types/units'

type GroupBy = 'sector' | 'type'

function hpBarColor(ratio: number): string {
  if (ratio > 0.6) return '#22c55e'
  if (ratio > 0.3) return '#eab308'
  return '#ef4444'
}

function UnitTile({ u, selected, onToggle, maxHp, requiresFuel }: {
  u: SIUnit; selected: boolean; onToggle: (e: React.MouseEvent) => void
  maxHp: number; requiresFuel: boolean
}) {
  const hpRatio = Math.max(0, Math.min(1, u.hp / maxHp))

  return (
    <div
      data-testid={`unit-row-${u.id}`}
      onClick={onToggle}
      className={`relative w-14 h-14 rounded cursor-pointer flex-shrink-0 flex flex-col items-center justify-center ${
        selected
          ? 'bg-blue-900 ring-1 ring-blue-400'
          : 'bg-gray-800 hover:bg-gray-700'
      }`}
      title={`${u.type} HP:${u.hp}/${maxHp} Mob:${u.mobility} A:${u.ammo}${requiresFuel ? ` F:${u.fuel}` : ''}${u.nextCoords ? ` → (${u.nextCoords.x},${u.nextCoords.y})` : ''}`}
    >
      {/* Unit icon */}
      <img
        src={unitIconPath(u.type, selected ? 'aqua' : 'aqua')}
        alt={u.type}
        className="w-7 h-7"
        draggable={false}
      />

      {/* HP bar */}
      <div className="w-10 h-1 bg-gray-600 rounded-full mt-0.5 overflow-hidden">
        <div
          className="h-full rounded-full"
          style={{ width: `${hpRatio * 100}%`, backgroundColor: hpBarColor(hpRatio) }}
        />
      </div>

      {/* Mobility */}
      <div className="text-[9px] text-gray-300 font-mono leading-tight">
        {u.mobility}
      </div>

      {/* Moving indicator */}
      {u.nextCoords && (
        <div className="absolute top-0 right-0.5 text-yellow-400 text-[9px] font-bold">
          →
        </div>
      )}

      {/* Low fuel/ammo warning */}
      {requiresFuel && u.fuel >= 0 && u.fuel <= 2 && (
        <div className="absolute top-0 left-0.5 text-red-500 text-[9px] font-bold">!</div>
      )}
      {!requiresFuel && u.ammo === 0 && (
        <div className="absolute top-0 left-0.5 text-orange-400 text-[9px] font-bold">!</div>
      )}
    </div>
  )
}

export default function UnitsTab() {
  const { state, toggleUnit, selectOnlyUnit, selectSector } = useGame()
  const { selectedCoords, selectedUnitIds, update, unitBases } = state
  const [groupBy, setGroupBy] = useState<GroupBy>('sector')
  const [hideNoMob, setHideNoMob] = useState(true)

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myNationId = update.nationId

  const allMyUnits = update.units.filter(u => u.nationId === myNationId)
  const visibleUnits = hideNoMob ? allMyUnits.filter(u => u.mobility > 0) : allMyUnits
  const hiddenCount = allMyUnits.length - visibleUnits.length

  const ubMap = new Map(unitBases.map(ub => [ub.type, ub]))

  if (allMyUnits.length === 0) {
    return <p className="text-gray-500">No units.</p>
  }

  function handleToggle(u: SIUnit, e: React.MouseEvent) {
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
    for (const u of visibleUnits) {
      const key = `${u.coords.x},${u.coords.y}`
      const list = map.get(key)
      if (list) list.push(u)
      else map.set(key, [u])
    }
    return [...map.entries()].sort((a, b) => a[0].localeCompare(b[0]))
  }

  const groupedByType = () => {
    const map = new Map<string, SIUnit[]>()
    for (const u of visibleUnits) {
      const list = map.get(u.type)
      if (list) list.push(u)
      else map.set(u.type, [u])
    }
    return [...map.entries()].sort((a, b) => a[0].localeCompare(b[0]))
  }

  return (
    <div data-testid="units-list" className="space-y-2">
      <div className="flex justify-between items-center">
        <h3 className="font-bold text-gray-300">Units ({visibleUnits.length}{hiddenCount > 0 ? `/${allMyUnits.length}` : ''})</h3>
        <div className="flex gap-1">
          <button
            onClick={() => setHideNoMob(!hideNoMob)}
            className={`text-xs px-2 py-0.5 rounded ${
              hideNoMob ? 'bg-green-700 text-white' : 'bg-gray-700 hover:bg-gray-600'
            }`}
            title={hideNoMob ? 'Showing units with mobility — click to show all' : 'Showing all units — click to hide 0 mobility'}
          >
            Mob
          </button>
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
              className={`text-xs cursor-pointer hover:underline mb-1 ${
                isSelected ? 'text-blue-300 font-semibold' : 'text-blue-400'
              }`}
              onClick={() => handleSelectSector(x, y)}
            >
              ({x}, {y}) — {units.length} unit{units.length !== 1 ? 's' : ''}
            </div>
            <div className="flex flex-wrap gap-1">
              {units.map(u => (
                <UnitTile
                  key={u.id}
                  u={u}
                  selected={selectedUnitIds.has(u.id)}
                  onToggle={(e) => handleToggle(u, e)}
                  maxHp={ubMap.get(u.type)?.hp ?? 10}
                  requiresFuel={ubMap.get(u.type)?.requiresFuel ?? false}

                />
              ))}
            </div>
          </div>
        )
      })}

      {groupBy === 'type' && groupedByType().map(([type, units]) => (
        <div key={type}>
          <div className="text-xs text-gray-300 font-semibold mb-1">
            {type} ({units.length})
          </div>
          <div className="flex flex-wrap gap-1">
            {units.map(u => (
              <UnitTile
                key={u.id}
                u={u}
                selected={selectedUnitIds.has(u.id)}
                onToggle={(e) => handleToggle(u, e)}
                maxHp={ubMap.get(u.type)?.hp ?? 10}
                requiresFuel={ubMap.get(u.type)?.requiresFuel ?? false}
              />
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}
