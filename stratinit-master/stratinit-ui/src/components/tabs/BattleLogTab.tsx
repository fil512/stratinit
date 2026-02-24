import { useGame } from '../../context/GameContext'

export default function BattleLogTab() {
  const { state, selectSector } = useGame()
  const { update } = state

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const logs = update.log
  if (logs.length === 0) {
    return <p className="text-gray-500">No battle log entries.</p>
  }

  return (
    <div data-testid="battle-log-list" className="space-y-1">
      <h3 className="font-bold text-gray-300">Battle Log ({logs.length})</h3>
      {logs.map((entry, index) => (
        <div
          key={entry.id}
          data-testid={`battle-log-entry-${index}`}
          className="p-1 bg-gray-800 rounded text-xs cursor-pointer hover:bg-gray-700"
          onClick={() => selectSector(entry.coords)}
        >
          <div className="flex justify-between">
            <span className="font-semibold">
              {entry.attackerUnit} vs {entry.defenderUnit}
            </span>
            <span className="text-gray-400">
              ({entry.coords.x},{entry.coords.y})
            </span>
          </div>
          <div>
            Dmg: {entry.damage}
            {entry.returnDamage > 0 && ` / Return: ${entry.returnDamage}`}
            {entry.attackerDied && ' [Attacker killed]'}
            {entry.defenderDied && ' [Defender killed]'}
          </div>
          {entry.messages.map((msg, i) => (
            <div key={i} className="text-gray-400">{msg}</div>
          ))}
        </div>
      ))}
    </div>
  )
}
