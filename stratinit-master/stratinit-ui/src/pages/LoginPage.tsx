import { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/auth'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    try {
      await login(username, password)
      navigate('/games')
    } catch {
      setError('Invalid username or password')
    }
  }

  return (
    <div className="max-w-sm mx-auto mt-24 font-sans">
      <h1 className="text-2xl font-bold mb-6">Strategic Initiative</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="username" className="block mb-1">Username</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="block mb-1">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>
        {error && <div className="text-red-600 mb-3">{error}</div>}
        <button type="submit" className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
          Login
        </button>
      </form>
    </div>
  )
}
