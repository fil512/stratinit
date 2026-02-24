import { useEffect, useState } from 'react'
import { getLeaderboard } from '../api/game'
import type { SIPlayerRank } from '../types/game'

export default function LeaderboardPage() {
  const [players, setPlayers] = useState<SIPlayerRank[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getLeaderboard()
      .then(setPlayers)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="max-w-2xl mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-4">Leaderboard</h1>
      {loading ? (
        <p className="text-gray-400">Loading...</p>
      ) : players.length === 0 ? (
        <p className="text-gray-400">No players yet.</p>
      ) : (
        <table data-testid="leaderboard-table" className="w-full border-collapse">
          <thead>
            <tr>
              <th className="text-left border-b border-gray-600 p-2">#</th>
              <th className="text-left border-b border-gray-600 p-2">Player</th>
              <th className="text-right border-b border-gray-600 p-2">Wins</th>
              <th className="text-right border-b border-gray-600 p-2">Played</th>
              <th className="text-right border-b border-gray-600 p-2">Win %</th>
            </tr>
          </thead>
          <tbody>
            {players.map((player, i) => (
              <tr key={player.username} className="hover:bg-gray-800">
                <td className="p-2 text-gray-400">{i + 1}</td>
                <td className="p-2">{player.username}</td>
                <td className="p-2 text-right">{player.wins}</td>
                <td className="p-2 text-right">{player.played}</td>
                <td className="p-2 text-right">{player.winPercentage}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
