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
        <Link to="/games" className="text-lg font-bold hover:text-blue-400">
          Strategic Initiative
        </Link>
        <button
          onClick={handleLogout}
          className="px-3 py-1 text-sm border border-gray-600 rounded hover:bg-gray-800"
        >
          Logout
        </button>
      </header>
      <main className="flex-1 overflow-hidden">
        <Outlet />
      </main>
    </div>
  )
}
