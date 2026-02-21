import { useState } from 'react'
import { useGame } from '../../context/GameContext'

export default function UnitsTab() {
  const {
    state, getUnitsAt, toggleUnit, selectSector,
    disbandSelectedUnits, cancelSelectedMoves,
    buildCityWithUnit, switchTerrainWithUnit,
    cedeSelectedUnits,
  } = useGame()
  const { selectedCoords, selectedUnitIds, update } = state
  const [cedeNationId, setCedeNationId] = useState<number | null>(null)

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myNationId = update.nationId

  if (!selectedCoords) {
    return <p className="text-gray-500">Click a sector to see units.</p>
  }

  const units = getUnitsAt(selectedCoords.x, selectedCoords.y)
    .filter(u => u.nationId === myNationId)

  if (units.length === 0) {
    return <p className="text-gray-500">No units here.</p>
  }

  const selectedUnits = units.filter(u => selectedUnitIds.has(u.id))
  const hasSelection = selectedUnits.length > 0
  const hasMovingUnits = selectedUnits.some(u => u.nextCoords !== null)
  const hasEngineer = selectedUnits.some(u => u.type === 'ENGINEER')
  const alliedNations = update.relations
    .filter(r => r.meToThem === 'ALLIED')
    .map(r => {
      const nation = update.nations.find(n => n.nationId === r.nationId)
      return nation ? { id: r.nationId, name: nation.name } : null
    })
    .filter((n): n is { id: number; name: string } => n !== null)

  function selectAll() {
    if (selectedCoords) {
      selectSector(selectedCoords)
    }
  }

  async function handleDisband() {
    if (!confirm(`Disband ${selectedUnits.length} unit(s)?`)) return
    await disbandSelectedUnits()
  }

  async function handleCede() {
    if (cedeNationId === null) return
    await cedeSelectedUnits(cedeNationId)
    setCedeNationId(null)
  }

  return (
    <div className="space-y-2">
      <div className="flex justify-between items-center">
        <h3 className="font-bold text-gray-300">
          Units at ({selectedCoords.x}, {selectedCoords.y})
        </h3>
        <button
          onClick={selectAll}
          className="text-xs px-2 py-0.5 bg-gray-700 rounded hover:bg-gray-600"
        >
          Select All
        </button>
      </div>

      {hasSelection && (
        <div className="space-y-1">
          <p className="text-xs text-blue-400">
            {selectedUnitIds.size} unit(s) selected — click map to move
          </p>
          <div className="flex flex-wrap gap-1">
            {hasMovingUnits && (
              <button
                onClick={cancelSelectedMoves}
                className="text-xs px-2 py-0.5 bg-yellow-800 rounded hover:bg-yellow-700"
              >
                Cancel Move
              </button>
            )}
            {hasEngineer && (
              <>
                <button
                  onClick={buildCityWithUnit}
                  className="text-xs px-2 py-0.5 bg-green-800 rounded hover:bg-green-700"
                >
                  Build City
                </button>
                <button
                  onClick={switchTerrainWithUnit}
                  className="text-xs px-2 py-0.5 bg-teal-800 rounded hover:bg-teal-700"
                >
                  Switch Terrain
                </button>
              </>
            )}
            <button
              onClick={handleDisband}
              className="text-xs px-2 py-0.5 bg-red-800 rounded hover:bg-red-700"
            >
              Disband
            </button>
          </div>
          {alliedNations.length > 0 && (
            <div className="flex items-center gap-1">
              <select
                value={cedeNationId ?? ''}
                onChange={e => setCedeNationId(e.target.value ? Number(e.target.value) : null)}
                className="text-xs bg-gray-800 border border-gray-600 rounded px-1 py-0.5"
              >
                <option value="">Cede to...</option>
                {alliedNations.map(n => (
                  <option key={n.id} value={n.id}>{n.name}</option>
                ))}
              </select>
              {cedeNationId !== null && (
                <button
                  onClick={handleCede}
                  className="text-xs px-2 py-0.5 bg-purple-800 rounded hover:bg-purple-700"
                >
                  Cede
                </button>
              )}
            </div>
          )}
        </div>
      )}

      {units.map(u => (
        <div
          key={u.id}
          onClick={() => toggleUnit(u.id)}
          className={`p-1 rounded cursor-pointer text-xs ${
            selectedUnitIds.has(u.id)
              ? 'bg-blue-900 border border-blue-500'
              : 'bg-gray-800 hover:bg-gray-700'
          }`}
        >
          <div className="font-semibold">{u.type}</div>
          <div>
            HP: {u.hp} | Mob: {u.mobility} | Ammo: {u.ammo} | Fuel: {u.fuel}
          </div>
          {u.nextCoords && (
            <div className="text-yellow-400">
              Moving to ({u.nextCoords.x}, {u.nextCoords.y})
            </div>
          )}
        </div>
      ))}
    </div>
  )
}
