import { useState, useEffect } from 'react'
import { useGame } from '../../context/GameContext'
import type { SIUnit, CityFieldToUpdate } from '../../types/game'
import UnitControls from './UnitControls'
import { unitIconPath, cityIconPath } from '../../types/units'

function BuildProgressBar({ lastUpdated, productionTime, tickIntervalMs }: {
  lastUpdated: string | null
  productionTime: number
  tickIntervalMs: number
}) {
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])

  if (!lastUpdated || productionTime <= 0 || tickIntervalMs <= 0) return null

  const startTime = new Date(lastUpdated).getTime()
  const totalMs = productionTime * tickIntervalMs
  const elapsed = now - startTime
  const progress = Math.min(Math.max(elapsed / totalMs, 0), 1)
  const remainingMs = Math.max(totalMs - elapsed, 0)
  const remainingSec = Math.ceil(remainingMs / 1000)
  const min = Math.floor(remainingSec / 60)
  const sec = remainingSec % 60
  const timeStr = min > 0 ? `${min}m ${sec}s` : `${sec}s`

  return (
    <div className="flex items-center gap-1.5 mt-1">
      <div className="flex-1 h-2 bg-gray-700 rounded overflow-hidden">
        <div
          className="h-full bg-green-500 transition-all duration-1000"
          style={{ width: `${progress * 100}%` }}
        />
      </div>
      <span className="text-xs text-gray-400 whitespace-nowrap">{timeStr}</span>
    </div>
  )
}

function hpBarColor(ratio: number): string {
  if (ratio > 0.6) return '#22c55e'
  if (ratio > 0.3) return '#eab308'
  return '#ef4444'
}

function UnitTile({ u, selected, onToggle, maxHp, requiresFuel, color }: {
  u: SIUnit; selected: boolean; onToggle: (e: React.MouseEvent) => void
  maxHp: number; requiresFuel: boolean; color: string
}) {
  const hpRatio = Math.max(0, Math.min(1, u.hp / maxHp))

  return (
    <div
      data-testid={`sector-unit-${u.id}`}
      onClick={onToggle}
      className={`relative w-14 h-14 rounded cursor-pointer flex-shrink-0 flex flex-col items-center justify-center ${
        selected
          ? 'bg-blue-900 ring-1 ring-blue-400'
          : 'bg-gray-800 hover:bg-gray-700'
      }`}
      title={`${u.type} HP:${u.hp}/${maxHp} Mob:${u.mobility} A:${u.ammo}${requiresFuel ? ` F:${u.fuel}` : ''}${u.nextCoords ? ` → (${u.nextCoords.x},${u.nextCoords.y})` : ''}`}
    >
      <img
        src={unitIconPath(u.type, color)}
        alt={u.type}
        className="w-7 h-7"
        draggable={false}
      />
      <div className="w-10 h-1 bg-gray-600 rounded-full mt-0.5 overflow-hidden">
        <div
          className="h-full rounded-full"
          style={{ width: `${hpRatio * 100}%`, backgroundColor: hpBarColor(hpRatio) }}
        />
      </div>
      <div className="text-[9px] text-gray-300 font-mono leading-tight">
        {u.mobility}
      </div>
      {u.nextCoords && (
        <div className="absolute top-0 right-0.5 text-yellow-400 text-[9px] font-bold">→</div>
      )}
      {requiresFuel && u.fuel >= 0 && u.fuel <= 2 && (
        <div className="absolute top-0 left-0.5 text-red-500 text-[9px] font-bold">!</div>
      )}
      {!requiresFuel && u.ammo === 0 && (
        <div className="absolute top-0 left-0.5 text-orange-400 text-[9px] font-bold">!</div>
      )}
    </div>
  )
}

export default function SectorTab() {
  const { state, getSectorAt, getUnitsAt, getCityAt, updateCityProduction, toggleUnit, selectOnlyUnit } = useGame()
  const { selectedCoords, selectedUnitIds, unitBases, update } = state

  if (!selectedCoords || !update) {
    return <p className="text-gray-500">Click a sector on the map.</p>
  }

  const sector = getSectorAt(selectedCoords.x, selectedCoords.y)
  const units = getUnitsAt(selectedCoords.x, selectedCoords.y)
  const city = getCityAt(selectedCoords.x, selectedCoords.y)
  const myNationId = update.nationId

  if (!sector) {
    return <p className="text-gray-500">Unknown sector.</p>
  }

  const isMyCity = city && city.nationId === myNationId
  const myNation = update.nations.find(n => n.nationId === myNationId)
  const myTech = myNation?.tech ?? 0
  const bs = state.boardSize
  const norm = (a: number) => ((a % bs) + bs) % bs
  // Even-Q hex neighbors matching server's SectorCoords.sectorsWithin
  const hexNeighbors = (x: number, y: number): [number, number][] => {
    const cx = x
    const cz = y - ((x + (x & 1)) >> 1)
    const result: [number, number][] = []
    for (let dq = -1; dq <= 1; dq++) {
      for (let dr = Math.max(-1, -dq - 1); dr <= Math.min(1, -dq + 1); dr++) {
        if (dq === 0 && dr === 0) continue
        const ds = -dq - dr
        const col = cx + dq
        const row = (cz + ds) + ((col + (col & 1)) >> 1)
        result.push([norm(col), norm(row)])
      }
    }
    return result
  }
  const isCoastal = isMyCity && hexNeighbors(selectedCoords.x, selectedCoords.y).some(([nx, ny]) => {
    const s = getSectorAt(nx, ny)
    return s && s.type === 'WATER'
  })
  const buildableUnits = isMyCity
    ? unitBases.filter(ub => ub.type !== 'BASE' && ub.tech <= myTech && (ub.builtIn !== 'PORT' || isCoastal))
    : []
  const myUnits = units.filter(u => u.nationId === myNationId)
  const enemyUnits = units.filter(u => u.nationId !== myNationId)
  const ubMap = new Map(unitBases.map(ub => [ub.type, ub]))

  function handleToggle(u: SIUnit, e: React.MouseEvent) {
    // Tap when units already selected → toggle (for iPad multi-select)
    if (e.shiftKey || selectedUnitIds.size > 0) {
      toggleUnit(u.id)
    } else {
      selectOnlyUnit(u.id)
    }
  }

  function handleBuildChange(field: CityFieldToUpdate, value: string) {
    if (city) {
      updateCityProduction(city, field, value)
    }
  }

  return (
    <div data-testid="sector-info" className="space-y-3">
      <div>
        <h3 className="font-bold text-gray-300">
          Sector ({selectedCoords.x}, {selectedCoords.y})
        </h3>
        <p>Terrain: {sector.type}</p>
        {sector.cityType && <p className="flex items-center">City: <img src={cityIconPath(sector.cityType, 'aqua')} alt="" className="w-4 h-4 mx-1" />{sector.cityType}{(() => {
          if (!city) return null
          if (city.nationId === myNationId) return null
          const owner = update.nations.find(n => n.nationId === city.nationId)
          return owner ? <span className="text-gray-400"> ({owner.name})</span> : null
        })()}</p>}
        {sector.flak > 0 && <p>Flak: {sector.flak}</p>}
        {sector.cannons > 0 && <p>Cannons: {sector.cannons}</p>}
      </div>

      {myUnits.length > 0 && (
        <div>
          <h4 className="font-semibold text-gray-300">My Units ({myUnits.length})</h4>
          <div className="flex flex-wrap gap-1 mt-1">
            {myUnits.map(u => (
              <UnitTile
                key={u.id}
                u={u}
                selected={selectedUnitIds.has(u.id)}
                onToggle={(e) => handleToggle(u, e)}
                maxHp={ubMap.get(u.type)?.hp ?? 10}
                requiresFuel={ubMap.get(u.type)?.requiresFuel ?? false}
                color="aqua"
              />
            ))}
          </div>
          <UnitControls />
        </div>
      )}

      {enemyUnits.length > 0 && (
        <div>
          <h4 className="font-semibold text-gray-300">Enemy Units ({enemyUnits.length})</h4>
          <div className="flex flex-wrap gap-1 mt-1">
            {enemyUnits.map(u => (
              <UnitTile
                key={u.id}
                u={u}
                selected={false}
                onToggle={() => {}}
                maxHp={ubMap.get(u.type)?.hp ?? 10}
                requiresFuel={ubMap.get(u.type)?.requiresFuel ?? false}
                color="red"
              />
            ))}
          </div>
        </div>
      )}

      {isMyCity && (
        <div>
          <h4 className="font-semibold text-gray-300">Production</h4>
          <label className="block mt-1">
            <span className="text-xs text-gray-400">Build:</span>
            <select
              data-testid="sector-build-select"
              value={city.build ?? ''}
              onChange={e => handleBuildChange('BUILD', e.target.value)}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
            >
              <option value="">-- None --</option>
              {(['BASE', 'FORT', 'PORT', 'AIRPORT', 'TECH'] as const).map(ct => {
                const group = buildableUnits.filter(ub => ub.builtIn === ct)
                return group.length > 0 ? (
                  <optgroup key={ct} label={ct}>
                    {group.map(ub => (
                      <option key={ub.type} value={ub.type}>{ub.type}</option>
                    ))}
                  </optgroup>
                ) : null
              })}
            </select>
            {city.build && (() => {
              const ub = unitBases.find(u => u.type === city.build)
              return ub ? (
                <BuildProgressBar
                  lastUpdated={city.lastUpdated}
                  productionTime={ub.productionTime}
                  tickIntervalMs={update.buildTickIntervalMs}
                />
              ) : null
            })()}
          </label>
          <label className="block mt-1">
            <span className="text-xs text-gray-400">Next Build:</span>
            <select
              data-testid="sector-next-build-select"
              value={city.nextBuild ?? ''}
              onChange={e => handleBuildChange('NEXT_BUILD', e.target.value)}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
            >
              <option value="">-- None --</option>
              {(['BASE', 'FORT', 'PORT', 'AIRPORT', 'TECH'] as const).map(ct => {
                const group = buildableUnits.filter(ub => ub.builtIn === ct)
                return group.length > 0 ? (
                  <optgroup key={ct} label={ct}>
                    {group.map(ub => (
                      <option key={ub.type} value={ub.type}>{ub.type}</option>
                    ))}
                  </optgroup>
                ) : null
              })}
            </select>
          </label>
        </div>
      )}
    </div>
  )
}
