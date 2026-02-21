import { useGame } from '../../context/GameContext'
import type { CityFieldToUpdate } from '../../types/game'

export default function SectorTab() {
  const { state, getSectorAt, getUnitsAt, getCityAt, updateCityProduction } = useGame()
  const { selectedCoords, unitBases, update } = state

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
  const buildableUnits = isMyCity
    ? unitBases.filter(ub => ub.builtIn === city.type)
    : []

  function handleBuildChange(field: CityFieldToUpdate, value: string) {
    if (city) {
      updateCityProduction(city, field, value)
    }
  }

  return (
    <div className="space-y-3">
      <div>
        <h3 className="font-bold text-gray-300">
          Sector ({selectedCoords.x}, {selectedCoords.y})
        </h3>
        <p>Terrain: {sector.type}</p>
        {sector.cityType && <p>City: {sector.cityType}</p>}
        {sector.flak > 0 && <p>Flak: {sector.flak}</p>}
        {sector.cannons > 0 && <p>Cannons: {sector.cannons}</p>}
      </div>

      {units.length > 0 && (
        <div>
          <h4 className="font-semibold text-gray-300">Units ({units.length})</h4>
          {units.map(u => (
            <div key={u.id} className="text-xs py-0.5">
              {u.type} HP:{u.hp} Mob:{u.mobility}
              {u.nationId !== myNationId && ' (enemy)'}
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
              value={city.build ?? ''}
              onChange={e => handleBuildChange('BUILD', e.target.value)}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
            >
              <option value="">-- None --</option>
              {buildableUnits.map(ub => (
                <option key={ub.type} value={ub.type}>{ub.type}</option>
              ))}
            </select>
          </label>
          <label className="block mt-1">
            <span className="text-xs text-gray-400">Next Build:</span>
            <select
              value={city.nextBuild ?? ''}
              onChange={e => handleBuildChange('NEXT_BUILD', e.target.value)}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
            >
              <option value="">-- None --</option>
              {buildableUnits.map(ub => (
                <option key={ub.type} value={ub.type}>{ub.type}</option>
              ))}
            </select>
          </label>
        </div>
      )}
    </div>
  )
}
