import { Outlet, Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'

export default function AppShell() {
  const navigate = useNavigate()

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
          <nav className="flex items-center gap-3 text-sm">
            <Link to="/games" className="text-gray-300 hover:text-blue-400">Games</Link>
            <Link to="/leaderboard" className="text-gray-300 hover:text-blue-400">Leaderboard</Link>
            <Link to="/rankings" className="text-gray-300 hover:text-blue-400">Rankings</Link>
            <Link to="/stats" className="text-gray-300 hover:text-blue-400">Stats</Link>
          </nav>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/settings" className="text-sm text-gray-300 hover:text-blue-400">Settings</Link>
          <button
            onClick={handleLogout}
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
