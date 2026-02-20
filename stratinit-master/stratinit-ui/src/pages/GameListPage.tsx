import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch } from '../api/client'
import { isLoggedIn, logout } from '../api/auth'

interface SIGame {
  id: number
  gamename: string
  started: boolean
  players: number
}

interface Result<T> {
  success: boolean
  value: T
  message: string
}

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

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>My Games</h1>
        <button onClick={handleLogout}>Logout</button>
      </div>
      {loading ? (
        <p>Loading...</p>
      ) : games.length === 0 ? (
        <p>No games joined yet.</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: 8 }}>ID</th>
              <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: 8 }}>Name</th>
              <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: 8 }}>Players</th>
              <th style={{ textAlign: 'left', borderBottom: '1px solid #ccc', padding: 8 }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {games.map(game => (
              <tr key={game.id}>
                <td style={{ padding: 8 }}>{game.id}</td>
                <td style={{ padding: 8 }}>{game.gamename}</td>
                <td style={{ padding: 8 }}>{game.players}</td>
                <td style={{ padding: 8 }}>{game.started ? 'In Progress' : 'Waiting'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
