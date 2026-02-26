import { useState, useEffect } from 'react'
import { useGame } from '../../context/GameContext'
import type { CityFieldToUpdate, SICityUpdate, SIUnitBase } from '../../types/game'
import { shrinkTime, formatCountdown } from '../../utils/time'

function BuildProgress({ city, unitBase, blitz, powerBlocked }: { city: SICityUpdate; unitBase: SIUnitBase | undefined; blitz: boolean; powerBlocked: boolean }) {
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])

  if (!city.build || !unitBase || !city.lastUpdated) return null
  if (city.build === 'BASE' || city.build === 'RESEARCH') return null

  const buildTimeMs = shrinkTime(blitz, unitBase.productionTime * 3600000)
  const lastUpdatedMs = new Date(city.lastUpdated).getTime()
  const elapsed = now - lastUpdatedMs
  const blocked = powerBlocked && elapsed >= buildTimeMs

  const elapsedInBuild = blocked ? buildTimeMs : elapsed % buildTimeMs
  const remaining = buildTimeMs - elapsedInBuild
  const progress = Math.min(elapsedInBuild / buildTimeMs, 1)

  return (
    <div className="flex items-center gap-1.5 ml-1 min-w-0">
      <div className="w-16 h-1.5 bg-gray-700 rounded-full overflow-hidden flex-shrink-0">
        <div
          className={`h-full rounded-full transition-[width] duration-1000 ease-linear ${blocked ? 'bg-red-500' : 'bg-blue-500'}`}
          style={{ width: `${progress * 100}%` }}
        />
      </div>
      {blocked ? (
        <span className="text-[10px] text-red-400 font-mono tabular-nums flex-shrink-0">POWER</span>
      ) : (
        <span className="text-[10px] text-gray-400 font-mono tabular-nums flex-shrink-0">
          {formatCountdown(remaining)}
        </span>
      )}
    </div>
  )
}

export default function CitiesTab() {
  const { state, selectSector, updateCityProduction } = useGame()
  const { update, unitBases } = state

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myCities = update.cities.filter(c => c.nationId === update.nationId)
  const myNation = update.nations.find(n => n.nationId === update.nationId)
  const myTech = myNation?.tech ?? 0
  const blitz = update.blitz ?? false
  const power = myNation?.power ?? 0
  const cityCount = myNation?.cities ?? myCities.length
  const powerBlocked = power >= cityCount * 5

  if (myCities.length === 0) {
    return <p className="text-gray-500">No cities.</p>
  }

  function handleBuildChange(cityIndex: number, field: CityFieldToUpdate, value: string) {
    const city = myCities[cityIndex]
    updateCityProduction(city, field, value)
  }

  return (
    <div data-testid="cities-list" className="space-y-2">
      <h3 className="font-bold text-gray-300">Cities ({myCities.length})</h3>
      {myCities.map((city, i) => {
        const buildable = unitBases.filter(ub => ub.builtIn === city.type && ub.tech <= myTech)
        const buildingUnit = city.build ? unitBases.find(ub => ub.type === city.build) : undefined
        return (
          <div
            key={`${city.coords.x},${city.coords.y}`}
            data-testid={`city-row-${city.coords.x}-${city.coords.y}`}
            className="p-1 bg-gray-800 rounded text-xs"
          >
            <div
              className="flex items-center cursor-pointer hover:text-blue-400"
              onClick={() => selectSector(city.coords)}
            >
              <span className="font-semibold">
                {city.type} ({city.coords.x}, {city.coords.y})
              </span>
              <BuildProgress city={city} unitBase={buildingUnit} blitz={blitz} powerBlocked={powerBlocked} />
            </div>
            <div className="flex gap-2 mt-1">
              <label className="flex-1">
                <span className="text-gray-400">Build:</span>
                <select
                  data-testid={`city-build-select-${city.coords.x}-${city.coords.y}`}
                  value={city.build ?? ''}
                  onChange={e => handleBuildChange(i, 'BUILD', e.target.value)}
                  className="block w-full bg-gray-700 border border-gray-600 rounded px-1 py-0.5"
                >
                  <option value="">--</option>
                  {buildable.map(ub => (
                    <option key={ub.type} value={ub.type}>{ub.type}</option>
                  ))}
                </select>
              </label>
              <label className="flex-1">
                <span className="text-gray-400">Next:</span>
                <select
                  data-testid={`city-next-build-select-${city.coords.x}-${city.coords.y}`}
                  value={city.nextBuild ?? ''}
                  onChange={e => handleBuildChange(i, 'NEXT_BUILD', e.target.value)}
                  className="block w-full bg-gray-700 border border-gray-600 rounded px-1 py-0.5"
                >
                  <option value="">--</option>
                  {buildable.map(ub => (
                    <option key={ub.type} value={ub.type}>{ub.type}</option>
                  ))}
                </select>
              </label>
            </div>
          </div>
        )
      })}
    </div>
  )
}
