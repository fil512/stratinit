import { useState, useEffect, FormEvent } from 'react'
import { apiFetch } from '../api/client'

interface PlayerProfile {
  username: string
  email: string
  emailGameMail: boolean
  created: number
  wins: number
  played: number
  winPerc: number
}

export default function SettingsPage() {
  const [profile, setProfile] = useState<PlayerProfile | null>(null)
  const [email, setEmail] = useState('')
  const [emailGameMail, setEmailGameMail] = useState(true)
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    apiFetch<PlayerProfile>('/stratinit/profile')
      .then(data => {
        setProfile(data)
        setEmail(data.email)
        setEmailGameMail(data.emailGameMail)
        setLoading(false)
      })
      .catch(() => {
        setError('Failed to load profile')
        setLoading(false)
      })
  }, [])

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSuccess('')

    if (password && password !== confirmPassword) {
      setError('Passwords do not match')
      return
    }

    setSubmitting(true)
    try {
      const body: { email: string; emailGameMail: boolean; password?: string } = {
        email,
        emailGameMail,
      }
      if (password) {
        body.password = password
      }
      const data = await apiFetch<{ message: string }>('/stratinit/profile', {
        method: 'PUT',
        body: JSON.stringify(body),
      })
      setSuccess(data.message)
      setPassword('')
      setConfirmPassword('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Update failed')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <div className="max-w-md mx-auto mt-12 text-gray-400">Loading...</div>
  }

  return (
    <div className="max-w-md mx-auto mt-12 px-4">
      <h1 className="text-2xl font-bold mb-6">Account Settings</h1>

      {profile && (
        <div className="mb-6 p-4 bg-gray-900 border border-gray-700 rounded">
          <div className="grid grid-cols-2 gap-2 text-sm">
            <span className="text-gray-400">Username</span>
            <span>{profile.username}</span>
            <span className="text-gray-400">Member since</span>
            <span>{new Date(profile.created).toLocaleDateString()}</span>
            <span className="text-gray-400">Games played</span>
            <span>{profile.played}</span>
            <span className="text-gray-400">Wins</span>
            <span>{profile.wins} ({profile.winPerc}%)</span>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="email" className="block mb-1 text-sm">Email</label>
          <input
            id="email"
            data-testid="settings-email-input"
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-100"
            required
          />
        </div>

        <div className="mb-4">
          <label className="flex items-center gap-2 text-sm cursor-pointer">
            <input
              type="checkbox"
              data-testid="settings-email-notifications-checkbox"
              checked={emailGameMail}
              onChange={e => setEmailGameMail(e.target.checked)}
              className="rounded"
            />
            Receive game mail notifications via email
          </label>
        </div>

        <hr className="border-gray-700 mb-4" />

        <p className="text-sm text-gray-400 mb-3">Leave password fields blank to keep current password.</p>

        <div className="mb-3">
          <label htmlFor="password" className="block mb-1 text-sm">New Password</label>
          <input
            id="password"
            data-testid="settings-password-input"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-100"
          />
        </div>

        <div className="mb-4">
          <label htmlFor="confirmPassword" className="block mb-1 text-sm">Confirm New Password</label>
          <input
            id="confirmPassword"
            data-testid="settings-confirm-password-input"
            type="password"
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-100"
          />
        </div>

        {error && <div className="text-red-400 mb-3 text-sm">{error}</div>}
        {success && <div className="text-green-400 mb-3 text-sm">{success}</div>}

        <button
          type="submit"
          data-testid="settings-save-button"
          disabled={submitting}
          className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  )
}
