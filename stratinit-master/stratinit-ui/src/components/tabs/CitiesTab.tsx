import { useGame } from '../../context/GameContext'
import type { CityFieldToUpdate } from '../../types/game'

export default function CitiesTab() {
  const { state, selectSector, updateCityProduction } = useGame()
  const { update, unitBases } = state

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myCities = update.cities.filter(c => c.nationId === update.nationId)

  if (myCities.length === 0) {
    return <p className="text-gray-500">No cities.</p>
  }

  function handleBuildChange(cityIndex: number, field: CityFieldToUpdate, value: string) {
    const city = myCities[cityIndex]
    updateCityProduction(city, field, value)
  }

  return (
    <div className="space-y-2">
      <h3 className="font-bold text-gray-300">Cities ({myCities.length})</h3>
      {myCities.map((city, i) => {
        const buildable = unitBases.filter(ub => ub.builtIn === city.type)
        return (
          <div
            key={`${city.coords.x},${city.coords.y}`}
            className="p-1 bg-gray-800 rounded text-xs"
          >
            <div
              className="font-semibold cursor-pointer hover:text-blue-400"
              onClick={() => selectSector(city.coords)}
            >
              {city.type} ({city.coords.x}, {city.coords.y})
            </div>
            <div className="flex gap-2 mt-1">
              <label className="flex-1">
                <span className="text-gray-400">Build:</span>
                <select
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
