import { useGame } from '../../context/GameContext'

export default function UnitsTab() {
  const { state, getUnitsAt, toggleUnit, selectSector } = useGame()
  const { selectedCoords, selectedUnitIds, update } = state

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

  function selectAll() {
    if (selectedCoords) {
      selectSector(selectedCoords) // re-selects all units
    }
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

      {selectedUnitIds.size > 0 && (
        <p className="text-xs text-blue-400">
          {selectedUnitIds.size} unit(s) selected — click map to move
        </p>
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
