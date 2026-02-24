import { useState, FormEvent } from 'react'
import { Link } from 'react-router-dom'

export default function ForgotPasswordPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSuccess('')

    if (!username && !email) {
      setError('Please enter your username or email address')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch('/stratinit/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username || null, email: email || null }),
      })
      const data = await response.json()
      if (!response.ok) {
        setError(data.error || 'Password reset failed')
        return
      }
      setSuccess(data.message)
    } catch {
      setError('Password reset failed')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-sm mx-auto mt-24 font-sans">
      <h1 className="text-2xl font-bold mb-2">Forgot Password</h1>
      <p className="text-sm text-gray-400 mb-6">
        Enter your username or email address and we'll send you a new password.
      </p>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="username" className="block mb-1">Username</label>
          <input
            id="username"
            data-testid="forgot-username-input"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            placeholder="Enter your username"
          />
        </div>
        <div className="mb-1 text-center text-sm text-gray-400">or</div>
        <div className="mb-3">
          <label htmlFor="email" className="block mb-1">Email</label>
          <input
            id="email"
            data-testid="forgot-email-input"
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            placeholder="Enter your email address"
          />
        </div>
        {error && <div className="text-red-600 mb-3">{error}</div>}
        {success && <div className="text-green-500 mb-3">{success}</div>}
        <button
          type="submit"
          data-testid="forgot-submit-button"
          disabled={submitting}
          className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? 'Sending...' : 'Reset Password'}
        </button>
      </form>
      <p className="mt-4 text-sm text-gray-400">
        Remember your password?{' '}
        <Link to="/login" className="text-blue-400 hover:underline">Login</Link>
      </p>
    </div>
  )
}
