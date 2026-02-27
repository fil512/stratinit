import { useState, useEffect } from 'react'
import { useGame } from '../../context/GameContext'
import type { CityFieldToUpdate } from '../../types/game'
import UnitControls from './UnitControls'
import { shrinkTime, formatCountdownShort } from '../../utils/time'

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
  const isCoastal = isMyCity && [[-1,0],[1,0],[0,-1],[0,1],[-1,-1],[-1,1],[1,-1],[1,1]].some(([dx,dy]) => {
    const s = getSectorAt(selectedCoords.x + dx, selectedCoords.y + dy)
    return s && s.type === 'WATER'
  })
  const buildableUnits = isMyCity
    ? unitBases.filter(ub => ub.type !== 'BASE' && ub.tech <= myTech && (ub.builtIn !== 'PORT' || isCoastal))
    : []
  const blitz = update.blitz ?? false
  const myUnits = units.filter(u => u.nationId === myNationId)
  const enemyUnits = units.filter(u => u.nationId !== myNationId)

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
        {sector.cityType && <p>City: {sector.cityType}{(() => {
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
          {myUnits.map(u => (
            <div
              key={u.id}
              data-testid={`sector-unit-${u.id}`}
              onClick={(e) => e.shiftKey ? toggleUnit(u.id) : selectOnlyUnit(u.id)}
              className={`text-xs py-0.5 px-1 rounded cursor-pointer ${
                selectedUnitIds.has(u.id)
                  ? 'bg-blue-900 border border-blue-500'
                  : 'hover:bg-gray-700'
              }`}
            >
              <div className="flex items-center flex-wrap">
                <span className="font-semibold">{u.type}</span>
                <span className="text-gray-400 ml-1">
                  HP:{u.hp} Mob:{u.mobility} A:{u.ammo}{unitBases.find(ub => ub.type === u.type)?.requiresFuel ? ` F:${u.fuel}` : ''}
                </span>
                <MobilityProgress lastUpdated={u.lastUpdated} blitz={blitz} />
                {u.nextCoords && (
                  <span className="text-yellow-400 ml-1">→ ({u.nextCoords.x},{u.nextCoords.y})</span>
                )}
              </div>
            </div>
          ))}
          <UnitControls />
        </div>
      )}

      {enemyUnits.length > 0 && (
        <div>
          <h4 className="font-semibold text-gray-300">Enemy Units ({enemyUnits.length})</h4>
          {enemyUnits.map(u => (
            <div key={u.id} className="text-xs py-0.5 text-red-400">
              {u.type} HP:{u.hp} Mob:{u.mobility}
            </div>
          ))}
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
