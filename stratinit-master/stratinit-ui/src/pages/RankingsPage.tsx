import { useEffect, useState } from 'react'
import { getTeamRankings } from '../api/game'
import type { SITeamRank } from '../types/game'

export default function RankingsPage() {
  const [teams, setTeams] = useState<SITeamRank[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getTeamRankings()
      .then(setTeams)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="max-w-3xl mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-4">Team Rankings</h1>
      {loading ? (
        <p className="text-gray-400">Loading...</p>
      ) : teams.length === 0 ? (
        <p className="text-gray-400">No rankings yet.</p>
      ) : (
        <table data-testid="rankings-table" className="w-full border-collapse">
          <thead>
            <tr>
              <th className="text-left border-b border-gray-600 p-2">#</th>
              <th className="text-left border-b border-gray-600 p-2">Team</th>
              <th className="text-right border-b border-gray-600 p-2">ELO</th>
              <th className="text-right border-b border-gray-600 p-2">Victories</th>
              <th className="text-right border-b border-gray-600 p-2">Opponents</th>
              <th className="text-right border-b border-gray-600 p-2">Wins</th>
              <th className="text-right border-b border-gray-600 p-2">Played</th>
            </tr>
          </thead>
          <tbody>
            {teams.map((team, i) => (
              <tr key={team.name} className="hover:bg-gray-800">
                <td className="p-2 text-gray-400">{i + 1}</td>
                <td className="p-2">{team.name}</td>
                <td className="p-2 text-right">{team.rank != null ? Math.round(team.rank) : '-'}</td>
                <td className="p-2 text-right">{team.victories}</td>
                <td className="p-2 text-right">{team.opponents}</td>
                <td className="p-2 text-right">{team.wins}</td>
                <td className="p-2 text-right">{team.played}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
