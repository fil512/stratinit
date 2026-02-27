import { useState } from 'react'
import { useGame } from '../../context/GameContext'
import type { RelationType } from '../../types/game'

const RELATION_OPTIONS: RelationType[] = ['WAR', 'NEUTRAL', 'FRIENDLY', 'ALLIED']

export default function PlayersTab() {
  const { state, changeRelation, concedeGame } = useGame()
  const { update } = state
  const [confirming, setConfirming] = useState(false)

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const myNationId = update.nationId
  const nations = update.nations
  const relations = update.relations

  async function handleConcede() {
    await concedeGame()
    setConfirming(false)
  }

  return (
    <div data-testid="players-list" className="space-y-1">
      <h3 className="font-bold text-gray-300">Players ({nations.length})</h3>
      {nations.map(nation => {
        const isMe = nation.nationId === myNationId
        const rel = relations.find(r => r.nationId === nation.nationId)
        return (
          <div key={nation.nationId} data-testid={`player-row-${nation.nationId}`} className="p-1 bg-gray-800 rounded text-xs">
            <div className="flex justify-between items-center">
              <span className={`font-semibold ${isMe ? 'text-cyan-400' : ''}`}>
                {nation.name}
                {isMe && ' (You)'}
              </span>
              <span className="text-gray-400">
                {nation.cities} cities | Power: {nation.power}
              </span>
            </div>
            <div>Tech: {nation.tech >= 0 ? nation.tech.toFixed(1) : '?'}</div>
            {!isMe && rel && (
              <div className="flex items-center gap-2 mt-1">
                <span className="text-gray-400">Relation (1 CP):</span>
                <select
                  data-testid={`relation-select-${nation.nationId}`}
                  value={rel.meToThem}
                  onChange={e => changeRelation(nation.nationId, e.target.value as RelationType)}
                  className="bg-gray-700 border border-gray-600 rounded px-1 py-0.5"
                >
                  {RELATION_OPTIONS.map(r => (
                    <option key={r} value={r}>{r}</option>
                  ))}
                </select>
                <span className="text-gray-500">
                  They: {rel.themToMe}
                </span>
              </div>
            )}
          </div>
        )
      })}
      <div className="pt-3 border-t border-gray-700">
        {confirming ? (
          <div className="flex items-center gap-2">
            <span className="text-red-400 text-xs">Are you sure? This cannot be undone.</span>
            <button
              onClick={handleConcede}
              className="px-2 py-1 text-xs bg-red-600 text-white rounded hover:bg-red-700"
            >
              Confirm
            </button>
            <button
              onClick={() => setConfirming(false)}
              className="px-2 py-1 text-xs border border-gray-600 rounded hover:bg-gray-800"
            >
              Cancel
            </button>
          </div>
        ) : (
          <button
            data-testid="concede-button"
            onClick={() => setConfirming(true)}
            className="px-3 py-1 text-xs border border-red-600 text-red-400 rounded hover:bg-red-900/30"
          >
            Concede
          </button>
        )}
      </div>
    </div>
  )
}
