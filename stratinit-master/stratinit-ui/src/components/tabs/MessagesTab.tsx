import { useState, useEffect, useCallback } from 'react'
import { useGame } from '../../context/GameContext'
import * as gameApi from '../../api/game'
import type { SIMessage } from '../../types/game'

type MailView = 'inbox' | 'sent' | 'announcements' | 'compose'

export default function MessagesTab() {
  const { state } = useGame()
  const { update } = state
  const [view, setView] = useState<MailView>('inbox')
  const [messages, setMessages] = useState<SIMessage[]>([])
  const [loading, setLoading] = useState(false)

  // Compose state
  const [toNationId, setToNationId] = useState<number>(-1)
  const [subject, setSubject] = useState('')
  const [body, setBody] = useState('')
  const [sending, setSending] = useState(false)

  const loadMessages = useCallback(async (v: MailView) => {
    if (v === 'compose') return
    setLoading(true)
    try {
      let msgs: SIMessage[]
      if (v === 'inbox') msgs = await gameApi.getMail()
      else if (v === 'sent') msgs = await gameApi.getSentMail()
      else msgs = await gameApi.getAnnouncements()
      setMessages(msgs)
    } catch {
      // Silently fail
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    loadMessages(view)
  }, [view, loadMessages])

  if (!update) return <p className="text-gray-500">No game loaded.</p>

  const nations = update.nations
  const myNationId = update.nationId

  function getNationName(nationId: number): string {
    if (nationId === -1 || nationId === 0) return 'All'
    const nation = nations.find(n => n.nationId === nationId)
    return nation?.name ?? `Nation ${nationId}`
  }

  async function handleSend() {
    if (!subject.trim()) return
    setSending(true)
    try {
      await gameApi.sendMessage({ toNationId, subject, body })
      setSubject('')
      setBody('')
      setToNationId(-1)
      setView('sent')
    } catch {
      // Silently fail
    }
    setSending(false)
  }

  const viewButtons: { key: MailView; label: string }[] = [
    { key: 'inbox', label: 'Inbox' },
    { key: 'sent', label: 'Sent' },
    { key: 'announcements', label: 'News' },
    { key: 'compose', label: 'New' },
  ]

  return (
    <div className="space-y-2">
      <div className="flex border-b border-gray-700 mb-1">
        {viewButtons.map(v => (
          <button
            key={v.key}
            data-testid={`messages-tab-${v.key === 'announcements' ? 'news' : v.key === 'compose' ? 'new' : v.key}`}
            onClick={() => setView(v.key)}
            className={`flex-1 px-1 py-1 text-xs font-medium ${
              view === v.key
                ? 'text-white border-b-2 border-blue-400'
                : 'text-gray-400 hover:text-gray-200'
            }`}
          >
            {v.label}
          </button>
        ))}
      </div>

      {view === 'compose' ? (
        <div className="space-y-2">
          <label className="block">
            <span className="text-xs text-gray-400">To:</span>
            <select
              data-testid="compose-to-select"
              value={toNationId}
              onChange={e => setToNationId(Number(e.target.value))}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
            >
              <option value={-1}>All (broadcast)</option>
              {nations.filter(n => n.nationId !== myNationId).map(n => (
                <option key={n.nationId} value={n.nationId}>{n.name}</option>
              ))}
            </select>
          </label>
          <label className="block">
            <span className="text-xs text-gray-400">Subject:</span>
            <input
              data-testid="compose-subject-input"
              value={subject}
              onChange={e => setSubject(e.target.value)}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-0.5 text-xs"
              placeholder="Subject..."
            />
          </label>
          <label className="block">
            <span className="text-xs text-gray-400">Body:</span>
            <textarea
              data-testid="compose-body-textarea"
              value={body}
              onChange={e => setBody(e.target.value)}
              rows={4}
              className="block w-full mt-0.5 bg-gray-800 border border-gray-600 rounded px-1 py-1 text-xs resize-none"
              placeholder="Message..."
            />
          </label>
          <button
            data-testid="compose-send-button"
            onClick={handleSend}
            disabled={sending || !subject.trim()}
            className="text-xs px-3 py-1 bg-blue-700 rounded hover:bg-blue-600 disabled:opacity-50"
          >
            {sending ? 'Sending...' : 'Send'}
          </button>
        </div>
      ) : loading ? (
        <p className="text-xs text-gray-500">Loading...</p>
      ) : messages.length === 0 ? (
        <p className="text-xs text-gray-500">No messages.</p>
      ) : (
        <div className="space-y-2">
          {messages.map(m => (
            <div key={m.messageId} className="bg-gray-800 rounded p-2 text-xs">
              <div className="flex justify-between text-gray-400">
                <span>
                  {view === 'sent' ? `To: ${getNationName(m.toNationId)}` : `From: ${getNationName(m.fromNationId)}`}
                </span>
                {m.date && (
                  <span>{new Date(m.date).toLocaleDateString()}</span>
                )}
              </div>
              <div className="font-semibold text-gray-200 mt-0.5">{m.subject}</div>
              {m.body && (
                <div className="text-gray-300 mt-1 whitespace-pre-wrap">{m.body}</div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
