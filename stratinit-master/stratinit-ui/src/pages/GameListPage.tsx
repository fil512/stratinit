import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { apiFetch } from '../api/client'
import { isLoggedIn } from '../api/auth'
import type { SIGame, Result } from '../types/game'

export default function GameListPage() {
  const [games, setGames] = useState<SIGame[]>([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login')
      return
    }
    apiFetch<Result<SIGame[]>>('/stratinit/joinedGames')
      .then(result => {
        if (result.success) {
          setGames(result.value)
        }
      })
      .catch(() => {
        navigate('/login')
      })
      .finally(() => setLoading(false))
  }, [navigate])

  return (
    <div className="max-w-2xl mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-4">My Games</h1>
      {loading ? (
        <p className="text-gray-400">Loading...</p>
      ) : games.length === 0 ? (
        <p className="text-gray-400">No games joined yet.</p>
      ) : (
        <table className="w-full border-collapse">
          <thead>
            <tr>
              <th className="text-left border-b border-gray-600 p-2">ID</th>
              <th className="text-left border-b border-gray-600 p-2">Name</th>
              <th className="text-left border-b border-gray-600 p-2">Players</th>
              <th className="text-left border-b border-gray-600 p-2">Status</th>
            </tr>
          </thead>
          <tbody>
            {games.map(game => (
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
    </div>
  )
}
