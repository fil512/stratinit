import { useState, useEffect } from 'react'
import { Outlet, Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'
import { apiFetch } from '../api/client'

export default function AppShell() {
  const navigate = useNavigate()
  const [isAdmin, setIsAdmin] = useState(false)

  useEffect(() => {
    apiFetch<{ admin?: boolean }>('/stratinit/profile')
      .then(data => {
        if (data.admin) setIsAdmin(true)
      })
      .catch(() => {})
  }, [])

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="h-screen flex flex-col bg-gray-950 text-gray-100">
      <header className="flex items-center justify-between px-4 py-2 bg-gray-900 border-b border-gray-700">
        <div className="flex items-center gap-4">
          <Link to="/games" className="text-lg font-bold hover:text-blue-400">
            Strategic Initiative
          </Link>
          <nav className="flex items-center gap-3 text-sm" aria-label="Main navigation">
            <Link to="/games" data-testid="nav-games-link" className="text-gray-300 hover:text-blue-400">Games</Link>
            <Link to="/leaderboard" data-testid="nav-leaderboard-link" className="text-gray-300 hover:text-blue-400">Leaderboard</Link>
            <Link to="/rankings" data-testid="nav-rankings-link" className="text-gray-300 hover:text-blue-400">Rankings</Link>
            <Link to="/stats" data-testid="nav-stats-link" className="text-gray-300 hover:text-blue-400">Stats</Link>
            {isAdmin && (
              <Link to="/admin" data-testid="nav-admin-link" className="text-yellow-400 hover:text-yellow-300">Admin</Link>
            )}
          </nav>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/settings" data-testid="nav-settings-link" className="text-sm text-gray-300 hover:text-blue-400">Settings</Link>
          <button
            onClick={handleLogout}
            data-testid="nav-logout-button"
            className="px-3 py-1 text-sm border border-gray-600 rounded hover:bg-gray-800"
          >
            Logout
          </button>
        </div>
      </header>
      <main className="flex-1 overflow-hidden">
        <Outlet />
      </main>
    </div>
  )
}
