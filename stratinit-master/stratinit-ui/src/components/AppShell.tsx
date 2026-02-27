import { useState, useEffect } from 'react'
import { Outlet, Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'
import { apiFetch } from '../api/client'
import TickProgressBar from './TickProgressBar'

export default function AppShell() {
  const navigate = useNavigate()
  const [isAdmin, setIsAdmin] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)

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
            <Link to="/games" data-testid="nav-games-link" className="text-gray-300 hover:text-blue-400">Lobby</Link>
            {isAdmin && (
              <Link to="/admin" data-testid="nav-admin-link" className="text-yellow-400 hover:text-yellow-300">Admin</Link>
            )}
          </nav>
        </div>
        <div className="flex items-center gap-3">
          <TickProgressBar />
          <div className="relative">
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              data-testid="nav-hamburger-button"
              className="p-1.5 text-gray-300 hover:text-white hover:bg-gray-800 rounded"
              aria-label="Menu"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
            {menuOpen && (
              <>
                <div className="fixed inset-0 z-10" onClick={() => setMenuOpen(false)} />
                <div className="absolute right-0 top-full mt-1 z-20 w-44 bg-gray-800 border border-gray-700 rounded shadow-lg py-1">
                  <Link to="/leaderboard" data-testid="nav-leaderboard-link" onClick={() => setMenuOpen(false)} className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white">Leaderboard</Link>
                  <Link to="/rankings" data-testid="nav-rankings-link" onClick={() => setMenuOpen(false)} className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white">Rankings</Link>
                  <Link to="/stats" data-testid="nav-stats-link" onClick={() => setMenuOpen(false)} className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white">Stats</Link>
                  <Link to="/settings" data-testid="nav-settings-link" onClick={() => setMenuOpen(false)} className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white">Settings</Link>
                  <div className="border-t border-gray-700 my-1" />
                  <button
                    onClick={() => { setMenuOpen(false); handleLogout() }}
                    data-testid="nav-logout-button"
                    className="block w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white"
                  >
                    Logout
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="flex-1 overflow-hidden">
        <Outlet />
      </main>
    </div>
  )
}
