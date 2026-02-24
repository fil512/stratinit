import { useState, useEffect, FormEvent } from 'react'
import { apiFetch } from '../api/client'

interface AdminPlayer {
  username: string
  email: string
  created: number
  lastLogin: number
  enabled: boolean
}

export default function AdminPage() {
  const [players, setPlayers] = useState<AdminPlayer[]>([])
  const [subject, setSubject] = useState('')
  const [body, setBody] = useState('')
  const [postError, setPostError] = useState('')
  const [postSuccess, setPostSuccess] = useState('')
  const [posting, setPosting] = useState(false)
  const [shutdownConfirm, setShutdownConfirm] = useState(false)
  const [shutdownMsg, setShutdownMsg] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiFetch<AdminPlayer[]>('/stratinit/admin/players')
      .then(data => {
        setPlayers(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [])

  async function handlePost(e: FormEvent) {
    e.preventDefault()
    setPostError('')
    setPostSuccess('')
    setPosting(true)
    try {
      const data = await apiFetch<{ message: string }>('/stratinit/admin/announcement', {
        method: 'POST',
        body: JSON.stringify({ subject, body }),
      })
      setPostSuccess(data.message)
      setSubject('')
      setBody('')
    } catch (err) {
      setPostError(err instanceof Error ? err.message : 'Post failed')
    } finally {
      setPosting(false)
    }
  }

  async function handleShutdown() {
    if (!shutdownConfirm) {
      setShutdownConfirm(true)
      return
    }
    try {
      const data = await apiFetch<{ message: string }>('/stratinit/admin/shutdown', {
        method: 'POST',
      })
      setShutdownMsg(data.message)
    } catch (err) {
      setShutdownMsg(err instanceof Error ? err.message : 'Shutdown failed')
    }
    setShutdownConfirm(false)
  }

  return (
    <div className="max-w-4xl mx-auto mt-8 px-4 space-y-8">
      <h1 className="text-2xl font-bold">Admin</h1>

      {/* Post Announcement */}
      <section>
        <h2 className="text-lg font-semibold mb-3">Post Announcement</h2>
        <form onSubmit={handlePost} className="space-y-3">
          <div>
            <label htmlFor="subject" className="block text-sm mb-1">Subject</label>
            <input
              id="subject"
              data-testid="admin-subject-input"
              type="text"
              value={subject}
              onChange={e => setSubject(e.target.value)}
              className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-100"
              required
            />
          </div>
          <div>
            <label htmlFor="body" className="block text-sm mb-1">Body</label>
            <textarea
              id="body"
              data-testid="admin-body-textarea"
              value={body}
              onChange={e => setBody(e.target.value)}
              rows={6}
              className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-100"
              required
            />
          </div>
          {postError && <div className="text-red-400 text-sm">{postError}</div>}
          {postSuccess && <div className="text-green-400 text-sm">{postSuccess}</div>}
          <button
            type="submit"
            data-testid="admin-post-button"
            disabled={posting}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {posting ? 'Posting...' : 'Post to All Games'}
          </button>
        </form>
      </section>

      {/* Player List */}
      <section>
        <h2 className="text-lg font-semibold mb-3">Players ({players.length})</h2>
        {loading ? (
          <div className="text-gray-400">Loading...</div>
        ) : (
          <div className="overflow-x-auto">
            <table data-testid="admin-players-table" className="w-full text-sm border-collapse">
              <thead>
                <tr className="border-b border-gray-700 text-left">
                  <th className="p-2">Username</th>
                  <th className="p-2">Email</th>
                  <th className="p-2">Created</th>
                  <th className="p-2">Last Login</th>
                  <th className="p-2">Enabled</th>
                </tr>
              </thead>
              <tbody>
                {players.map(p => (
                  <tr key={p.username} className="border-b border-gray-800 hover:bg-gray-900">
                    <td className="p-2">{p.username}</td>
                    <td className="p-2 text-gray-400">{p.email}</td>
                    <td className="p-2 text-gray-400">{p.created ? new Date(p.created).toLocaleDateString() : '-'}</td>
                    <td className="p-2 text-gray-400">{p.lastLogin ? new Date(p.lastLogin).toLocaleDateString() : '-'}</td>
                    <td className="p-2">{p.enabled ? 'Yes' : 'No'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* Server Shutdown */}
      <section>
        <h2 className="text-lg font-semibold mb-3">Server</h2>
        <button
          data-testid="admin-shutdown-button"
          onClick={handleShutdown}
          className={`px-4 py-2 rounded text-white ${
            shutdownConfirm
              ? 'bg-red-700 hover:bg-red-800'
              : 'bg-red-600 hover:bg-red-700'
          }`}
        >
          {shutdownConfirm ? 'Click again to confirm shutdown' : 'Shutdown Server'}
        </button>
        {shutdownMsg && <div className="mt-2 text-sm text-yellow-400">{shutdownMsg}</div>}
      </section>
    </div>
  )
}
