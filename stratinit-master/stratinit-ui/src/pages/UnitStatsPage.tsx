import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getGameUnitLove, getPlayerUnits, getGamePlayers, getCompletedGames } from '../api/game'
import { isLoggedIn } from '../api/auth'
import type { SIUnitLove, SIUnitDayRow, SIGameHistory } from '../types/game'
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  AreaChart, Area, XAxis, YAxis, CartesianGrid,
} from 'recharts'

const UNIT_BASE_TYPES = ['LAND', 'NAVY', 'AIR', 'TECH'] as const

const COLORS = [
  '#3b82f6', '#ef4444', '#22c55e', '#f59e0b', '#8b5cf6',
  '#ec4899', '#06b6d4', '#f97316', '#14b8a6', '#6366f1',
  '#a855f7', '#84cc16', '#e11d48', '#0ea5e9', '#d946ef',
  '#10b981', '#f43f5e', '#7c3aed', '#eab308', '#64748b',
  '#dc2626', '#059669', '#2563eb',
]

export default function UnitStatsPage() {
  const { gameId: gameIdParam } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const [games, setGames] = useState<SIGameHistory[]>([])
  const [selectedGameId, setSelectedGameId] = useState<number | null>(gameIdParam ? Number(gameIdParam) : null)
  const [unitLove, setUnitLove] = useState<SIUnitLove[]>([])
  const [players, setPlayers] = useState<string[]>([])
  const [selectedPlayer, setSelectedPlayer] = useState<string>('')
  const [playerData, setPlayerData] = useState<Record<string, SIUnitDayRow[]>>({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login')
      return
    }
    getCompletedGames()
      .then(setGames)
      .catch(() => navigate('/login'))
      .finally(() => setLoading(false))
  }, [navigate])

  useEffect(() => {
    if (selectedGameId == null) return
    setUnitLove([])
    setPlayers([])
    setSelectedPlayer('')
    setPlayerData({})
    Promise.all([
      getGameUnitLove(selectedGameId),
      getGamePlayers(selectedGameId),
    ]).then(([love, playerNames]) => {
      setUnitLove(love)
      setPlayers(playerNames)
      if (playerNames.length > 0) {
        setSelectedPlayer(playerNames[0])
      }
    })
  }, [selectedGameId])

  useEffect(() => {
    if (selectedGameId == null || !selectedPlayer) return
    setPlayerData({})
    Promise.all(
      UNIT_BASE_TYPES.map(type =>
        getPlayerUnits(selectedGameId, type, selectedPlayer).then(data => ({ type, data }))
      )
    ).then(results => {
      const data: Record<string, SIUnitDayRow[]> = {}
      for (const { type, data: rows } of results) {
        data[type] = rows
      }
      setPlayerData(data)
    })
  }, [selectedGameId, selectedPlayer])

  if (loading) {
    return (
      <div className="max-w-5xl mx-auto mt-10">
        <p className="text-gray-400">Loading...</p>
      </div>
    )
  }

  const pieData = unitLove.filter(u => u.love > 0)

  function getUnitTypeKeys(rows: SIUnitDayRow[]): string[] {
    if (rows.length === 0) return []
    return Object.keys(rows[0].counts)
  }

  function renderAreaChart(type: string, rows: SIUnitDayRow[]) {
    const keys = getUnitTypeKeys(rows)
    if (keys.length === 0) return null
    const hasData = rows.some(row => Object.values(row.counts).some(v => v > 0))
    if (!hasData) return null

    return (
      <div key={type} className="mb-8">
        <h3 className="text-lg font-semibold mb-2">{type} Units Built Per Day</h3>
        <ResponsiveContainer width="100%" height={300}>
          <AreaChart data={rows}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis dataKey="day" stroke="#9ca3af" label={{ value: 'Day', position: 'insideBottom', offset: -5, fill: '#9ca3af' }} />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', color: '#f3f4f6' }}
              labelFormatter={(label) => `Day ${label}`}
            />
            <Legend />
            {keys.map((unitType, i) => (
              <Area
                key={unitType}
                type="monotone"
                dataKey={`counts.${unitType}`}
                name={unitType.replace(/_/g, ' ')}
                stackId="1"
                stroke={COLORS[i % COLORS.length]}
                fill={COLORS[i % COLORS.length]}
                fillOpacity={0.6}
              />
            ))}
          </AreaChart>
        </ResponsiveContainer>
      </div>
    )
  }

  return (
    <div className="max-w-5xl mx-auto mt-6 px-4 overflow-y-auto" style={{ maxHeight: 'calc(100vh - 56px)' }}>
      <h1 className="text-2xl font-bold mb-4">Unit Statistics</h1>

      <div className="mb-6">
        <label className="text-sm text-gray-400 mr-2">Game:</label>
        <select
          value={selectedGameId ?? ''}
          onChange={e => {
            const id = Number(e.target.value)
            setSelectedGameId(id)
            navigate(`/stats/${id}`, { replace: true })
          }}
          className="bg-gray-800 border border-gray-600 rounded px-3 py-1 text-gray-100"
        >
          <option value="" disabled>Select a game</option>
          {games.map(g => (
            <option key={g.gameId} value={g.gameId}>
              {g.gamename} (#{g.gameId})
            </option>
          ))}
        </select>
      </div>

      {selectedGameId != null && pieData.length > 0 && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-2">Unit Love</h2>
          <p className="text-sm text-gray-400 mb-3">Weighted by count, tech factor, and production time</p>
          <ResponsiveContainer width="100%" height={400}>
            <PieChart>
              <Pie
                data={pieData}
                dataKey="love"
                nameKey="unitType"
                cx="50%"
                cy="50%"
                outerRadius={150}
                label={({ name, percent }: { name?: string; percent?: number }) =>
                  `${(name ?? '').replace(/_/g, ' ')} ${((percent ?? 0) * 100).toFixed(0)}%`
                }
              >
                {pieData.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', color: '#f3f4f6' }}
                formatter={(value) => [String(value), 'Love']}
              />
              <Legend formatter={(value: string) => value.replace(/_/g, ' ')} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}

      {selectedGameId != null && players.length > 0 && (
        <>
          <div className="mb-4">
            <label className="text-sm text-gray-400 mr-2">Player:</label>
            <select
              value={selectedPlayer}
              onChange={e => setSelectedPlayer(e.target.value)}
              className="bg-gray-800 border border-gray-600 rounded px-3 py-1 text-gray-100"
            >
              {players.map(p => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </div>

          {UNIT_BASE_TYPES.map(type => {
            const rows = playerData[type]
            if (!rows) return null
            return renderAreaChart(type, rows)
          })}
        </>
      )}

      {selectedGameId != null && unitLove.length === 0 && !loading && (
        <p className="text-gray-400">No unit data available for this game.</p>
      )}
    </div>
  )
}
