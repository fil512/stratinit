import { useState, FormEvent } from 'react'
import { useNavigate, Link } from 'react-router-dom'

export default function RegistrationPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')

    if (password !== confirmPassword) {
      setError('Passwords do not match')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch('/stratinit/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password }),
      })
      const data = await response.json()
      if (!response.ok) {
        setError(data.error || 'Registration failed')
        return
      }
      navigate('/login')
    } catch {
      setError('Registration failed')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-sm mx-auto mt-24 font-sans">
      <h1 className="text-2xl font-bold mb-6">Register</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="username" className="block mb-1">Username</label>
          <input
            id="username"
            data-testid="register-username-input"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="email" className="block mb-1">Email</label>
          <input
            id="email"
            data-testid="register-email-input"
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="block mb-1">Password</label>
          <input
            id="password"
            data-testid="register-password-input"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="confirmPassword" className="block mb-1">Confirm Password</label>
          <input
            id="confirmPassword"
            data-testid="register-confirm-password-input"
            type="password"
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
          />
        </div>
        {error && <div data-testid="register-error" className="text-red-600 mb-3">{error}</div>}
        <button
          type="submit"
          data-testid="register-submit-button"
          disabled={submitting}
          className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? 'Registering...' : 'Register'}
        </button>
      </form>
      <p className="mt-4 text-sm text-gray-400">
        Already have an account?{' '}
        <Link to="/login" className="text-blue-400 hover:underline">Login</Link>
      </p>
    </div>
  )
}
