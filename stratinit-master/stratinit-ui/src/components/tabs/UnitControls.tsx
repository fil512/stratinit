import { useState } from 'react'
import { useGame } from '../../context/GameContext'

export default function UnitControls() {
  const {
    state,
    disbandSelectedUnits, cancelSelectedMoves,
    buildCityWithUnit, switchTerrainWithUnit,
    cedeSelectedUnits,
  } = useGame()
  const { selectedUnitIds, update } = state
  const [cedeNationId, setCedeNationId] = useState<number | null>(null)

  if (!update || selectedUnitIds.size === 0) return null

  const selectedUnits = update.units.filter(u => selectedUnitIds.has(u.id))
  if (selectedUnits.length === 0) return null

  const hasMovingUnits = selectedUnits.some(u => u.nextCoords !== null)
  const hasEngineer = selectedUnits.some(u => u.type === 'ENGINEER')
  const MOB_COST_BUILD_CITY = 9
  const MOB_COST_SWITCH_TERRAIN = 6
  const canBuildCity = selectedUnits.some(u => u.type === 'ENGINEER' && u.mobility >= MOB_COST_BUILD_CITY)
  const canSwitchTerrain = selectedUnits.some(u => u.type === 'ENGINEER' && u.mobility >= MOB_COST_SWITCH_TERRAIN)
  const alliedNations = update.relations
    .filter(r => r.meToThem === 'ALLIED')
    .map(r => {
      const nation = update.nations.find(n => n.nationId === r.nationId)
      return nation ? { id: r.nationId, name: nation.name } : null
    })
    .filter((n): n is { id: number; name: string } => n !== null)

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
    <div className="space-y-1">
      <p className="text-xs text-blue-400">
        {selectedUnitIds.size} unit(s) selected — click map to move (1 CP)
      </p>
      <div className="flex flex-wrap gap-1">
        {hasMovingUnits && (
          <button
            data-testid="units-cancel-move-button"
            onClick={cancelSelectedMoves}
            className="text-xs px-2 py-0.5 bg-yellow-800 rounded hover:bg-yellow-700"
          >
            Cancel Move (1 CP)
          </button>
        )}
        {hasEngineer && (
          <>
            <button
              data-testid="units-build-city-button"
              onClick={buildCityWithUnit}
              disabled={!canBuildCity}
              className={`text-xs px-2 py-0.5 rounded ${
                canBuildCity
                  ? 'bg-green-800 hover:bg-green-700'
                  : 'bg-gray-700 text-gray-500 cursor-not-allowed'
              }`}
              title={canBuildCity ? undefined : `Needs ${MOB_COST_BUILD_CITY} mobility`}
            >
              Build City (128 CP)
            </button>
            <button
              data-testid="units-switch-terrain-button"
              onClick={switchTerrainWithUnit}
              disabled={!canSwitchTerrain}
              className={`text-xs px-2 py-0.5 rounded ${
                canSwitchTerrain
                  ? 'bg-teal-800 hover:bg-teal-700'
                  : 'bg-gray-700 text-gray-500 cursor-not-allowed'
              }`}
              title={canSwitchTerrain ? undefined : `Needs ${MOB_COST_SWITCH_TERRAIN} mobility`}
            >
              Switch Terrain (16 CP)
            </button>
          </>
        )}
        <button
          data-testid="units-disband-button"
          onClick={handleDisband}
          className="text-xs px-2 py-0.5 bg-red-800 rounded hover:bg-red-700"
        >
          Disband (1 CP)
        </button>
      </div>
      {alliedNations.length > 0 && (
        <div className="flex items-center gap-1">
          <select
            data-testid="units-cede-select"
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
              data-testid="units-cede-button"
              onClick={handleCede}
              className="text-xs px-2 py-0.5 bg-purple-800 rounded hover:bg-purple-700"
            >
              Cede (1 CP)
            </button>
          )}
        </div>
      )}
    </div>
  )
}
