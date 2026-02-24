import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { getJoinedGames, getUnjoinedGames, joinGame } from '../api/game'
import { isLoggedIn } from '../api/auth'
import type { SIGame } from '../types/game'

export default function GameListPage() {
  const [joinedGames, setJoinedGames] = useState<SIGame[]>([])
  const [unjoinedGames, setUnjoinedGames] = useState<SIGame[]>([])
  const [loading, setLoading] = useState(true)
  const [joining, setJoining] = useState<number | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login')
      return
    }
    Promise.all([getJoinedGames(), getUnjoinedGames()])
      .then(([joined, unjoined]) => {
        setJoinedGames(joined)
        setUnjoinedGames(unjoined)
      })
      .catch(() => {
        navigate('/login')
      })
      .finally(() => setLoading(false))
  }, [navigate])

  async function handleJoin(gameId: number) {
    setJoining(gameId)
    try {
      await joinGame(gameId, false)
      navigate(`/game/${gameId}`)
    } catch {
      setJoining(null)
    }
  }

  if (loading) {
    return (
      <div className="max-w-2xl mx-auto mt-10">
        <p className="text-gray-400">Loading...</p>
      </div>
    )
  }

  return (
    <div className="max-w-2xl mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-4">My Games</h1>
      {joinedGames.length === 0 ? (
        <p className="text-gray-400">No games joined yet.</p>
      ) : (
        <table data-testid="my-games-table" className="w-full border-collapse mb-8">
          <thead>
            <tr>
              <th className="text-left border-b border-gray-600 p-2">ID</th>
              <th className="text-left border-b border-gray-600 p-2">Name</th>
              <th className="text-left border-b border-gray-600 p-2">Players</th>
              <th className="text-left border-b border-gray-600 p-2">Status</th>
            </tr>
          </thead>
          <tbody>
            {joinedGames.map(game => (
              <tr key={game.id} className="hover:bg-gray-800">
                <td className="p-2">{game.id}</td>
                <td className="p-2">
                  <Link to={`/game/${game.id}`} className="text-blue-400 hover:underline">
                    {game.name}
                  </Link>
                </td>
                <td className="p-2">{game.players}</td>
                <td className="p-2">{game.started ? 'In Progress' : 'Waiting'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h2 className="text-xl font-bold mb-4">Available Games</h2>
      {unjoinedGames.length === 0 ? (
        <p className="text-gray-400">No games available to join.</p>
      ) : (
        <table data-testid="available-games-table" className="w-full border-collapse">
          <thead>
            <tr>
              <th className="text-left border-b border-gray-600 p-2">ID</th>
              <th className="text-left border-b border-gray-600 p-2">Name</th>
              <th className="text-left border-b border-gray-600 p-2">Players</th>
              <th className="text-left border-b border-gray-600 p-2">Size</th>
              <th className="text-left border-b border-gray-600 p-2"></th>
            </tr>
          </thead>
          <tbody>
            {unjoinedGames.map(game => (
              <tr key={game.id} className="hover:bg-gray-800">
                <td className="p-2">{game.id}</td>
                <td className="p-2">{game.name}</td>
                <td className="p-2">{game.players}</td>
                <td className="p-2">{game.size}</td>
                <td className="p-2">
                  <button
                    data-testid={`join-game-button-${game.id}`}
                    onClick={() => handleJoin(game.id)}
                    disabled={joining === game.id}
                    className="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
                  >
                    {joining === game.id ? 'Joining...' : 'Join'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
