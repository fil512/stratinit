import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getGameUnitLove, getPlayerUnits, getGamePlayers, getCompletedGames, getGameDetail, getGameTimeSeries } from '../api/game'
import { isLoggedIn } from '../api/auth'
import type { SIUnitLove, SIUnitDayRow, SIGameHistory, SIGameStats, SINationSnapshot } from '../types/game'
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  AreaChart, Area, XAxis, YAxis, CartesianGrid,
  BarChart, Bar, LineChart, Line,
} from 'recharts'

const UNIT_BASE_TYPES = ['LAND', 'NAVY', 'AIR', 'TECH'] as const

const COLORS = [
  '#3b82f6', '#ef4444', '#22c55e', '#f59e0b', '#8b5cf6',
  '#ec4899', '#06b6d4', '#f97316', '#14b8a6', '#6366f1',
  '#a855f7', '#84cc16', '#e11d48', '#0ea5e9', '#d946ef',
  '#10b981', '#f43f5e', '#7c3aed', '#eab308', '#64748b',
  '#dc2626', '#059669', '#2563eb',
]

const NATION_COLORS = ['#f59e0b', '#3b82f6', '#22c55e', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4', '#f97316']

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hours = d.getHours()
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const ampm = hours >= 12 ? 'PM' : 'AM'
  const h = hours % 12 || 12
  return `${month}/${day} ${h}:${minutes} ${ampm}`
}

function formatDuration(startStr: string | null, endStr: string | null): string {
  if (!startStr || !endStr) return '—'
  const ms = new Date(endStr).getTime() - new Date(startStr).getTime()
  const hours = Math.floor(ms / 3600000)
  const minutes = Math.floor((ms % 3600000) / 60000)
  if (hours >= 24) {
    const days = Math.floor(hours / 24)
    const remainingHours = hours % 24
    return `${days}d ${remainingHours}h ${minutes}m`
  }
  return `${hours}h ${minutes}m`
}

export default function UnitStatsPage() {
  const { gameId: gameIdParam } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const [games, setGames] = useState<SIGameHistory[]>([])
  const [selectedGameId, setSelectedGameId] = useState<number | null>(gameIdParam ? Number(gameIdParam) : null)
  const [gameStats, setGameStats] = useState<SIGameStats | null>(null)
  const [unitLove, setUnitLove] = useState<SIUnitLove[]>([])
  const [players, setPlayers] = useState<string[]>([])
  const [selectedPlayer, setSelectedPlayer] = useState<string>('')
  const [playerData, setPlayerData] = useState<Record<string, SIUnitDayRow[]>>({})
  const [snapshots, setSnapshots] = useState<SINationSnapshot[]>([])
  const [navalByNation, setNavalByNation] = useState<Record<string, number>[]>([])
  const [navalNationNames, setNavalNationNames] = useState<string[]>([])
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
    setGameStats(null)
    setUnitLove([])
    setPlayers([])
    setSelectedPlayer('')
    setPlayerData({})
    setSnapshots([])
    setNavalByNation([])
    setNavalNationNames([])
    Promise.all([
      getGameDetail(selectedGameId),
      getGameUnitLove(selectedGameId),
      getGamePlayers(selectedGameId),
      getGameTimeSeries(selectedGameId),
    ]).then(([detail, love, playerNames, snapshotData]) => {
      setGameStats(detail)
      setUnitLove(love)
      setPlayers(playerNames)
      setSnapshots(snapshotData)
      if (playerNames.length > 0) {
        setSelectedPlayer(playerNames[0])
      }
      // Fetch naval units built per day for all players
      Promise.all(
        playerNames.map(name =>
          getPlayerUnits(selectedGameId!, 'NAVY', name).then(rows => ({ name, rows }))
        )
      ).then(results => {
        const dayMap = new Map<number, Record<string, number>>()
        const names: string[] = []
        for (const { name, rows } of results) {
          names.push(name)
          for (const row of rows) {
            if (!dayMap.has(row.day)) {
              dayMap.set(row.day, { day: row.day })
            }
            const entry = dayMap.get(row.day)!
            const total = Object.values(row.counts).reduce((s, v) => s + v, 0)
            entry[name] = total
          }
        }
        // Fill missing days with 0
        const data = Array.from(dayMap.values()).sort((a, b) => a.day - b.day)
        for (const row of data) {
          for (const name of names) {
            if (!(name in row)) row[name] = 0
          }
        }
        setNavalByNation(data)
        setNavalNationNames(names.sort())
      })
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

  // Pivot snapshots into per-tick rows with nation names as keys
  function buildTimeSeriesData(metric: keyof SINationSnapshot) {
    const tickMap = new Map<number, Record<string, number>>()
    const nationNames = new Set<string>()
    for (const s of snapshots) {
      nationNames.add(s.nationName)
      if (!tickMap.has(s.tickNumber)) {
        tickMap.set(s.tickNumber, { tick: s.tickNumber })
      }
      const row = tickMap.get(s.tickNumber)!
      row[s.nationName] = s[metric] as number
    }
    const data = Array.from(tickMap.values()).sort((a, b) => a.tick - b.tick)
    return { data, nationNames: Array.from(nationNames).sort() }
  }

  function renderTimeSeriesChart(title: string, metric: keyof SINationSnapshot) {
    const { data, nationNames } = buildTimeSeriesData(metric)
    if (data.length === 0) return null
    return (
      <div className="mb-8">
        <h3 className="text-lg font-semibold mb-2">{title}</h3>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis dataKey="tick" stroke="#9ca3af" label={{ value: 'Tick', position: 'insideBottom', offset: -5, fill: '#9ca3af' }} />
            <YAxis stroke="#9ca3af" />
            <Tooltip
              contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', color: '#f3f4f6' }}
              labelFormatter={(label) => `Tick ${label}`}
            />
            <Legend />
            {nationNames.map((name, i) => (
              <Line
                key={name}
                type="monotone"
                dataKey={name}
                stroke={NATION_COLORS[i % NATION_COLORS.length]}
                dot={false}
                strokeWidth={2}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    )
  }

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
      <h1 className="text-2xl font-bold mb-4">Game Statistics</h1>

      <div className="mb-6">
        <label className="text-sm text-gray-400 mr-2">Game:</label>
        <select
          data-testid="stats-game-select"
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

      {/* Game Summary */}
      {gameStats && (
        <div className="mb-8 p-4 bg-gray-800 rounded-lg border border-gray-700">
          <h2 className="text-xl font-bold mb-3 text-gray-100">{gameStats.gamename} <span className="text-gray-400 font-normal text-base">#{gameStats.gameId}</span></h2>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-4">
            <div>
              <div className="text-xs text-gray-500 uppercase">Players</div>
              <div className="text-lg font-semibold">{gameStats.gamesize}</div>
            </div>
            <div>
              <div className="text-xs text-gray-500 uppercase">Type</div>
              <div className="text-lg font-semibold">{gameStats.blitz ? 'Blitz' : 'Standard'}</div>
            </div>
            <div>
              <div className="text-xs text-gray-500 uppercase">Started</div>
              <div className="text-sm">{formatDate(gameStats.startTime)}</div>
            </div>
            <div>
              <div className="text-xs text-gray-500 uppercase">Ended</div>
              <div className="text-sm">{formatDate(gameStats.ends)}</div>
            </div>
          </div>
          <div className="text-sm text-gray-400">
            Duration: {formatDuration(gameStats.startTime, gameStats.ends)}
          </div>
        </div>
      )}

      {/* Final Standings */}
      {gameStats && gameStats.nations.length > 0 && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-3">Final Standings</h2>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  <th className="text-left text-xs text-gray-400 pb-2 border-b border-gray-700">#</th>
                  <th className="text-left text-xs text-gray-400 pb-2 border-b border-gray-700">Nation</th>
                  <th className="text-right text-xs text-gray-400 pb-2 border-b border-gray-700">Cities</th>
                  <th className="text-right text-xs text-gray-400 pb-2 border-b border-gray-700">Power</th>
                </tr>
              </thead>
              <tbody>
                {gameStats.nations.map((n, i) => (
                  <tr key={n.name} className={i === 0 ? 'text-yellow-300 font-semibold' : ''}>
                    <td className="py-1.5 pr-2">{i + 1}</td>
                    <td className="py-1.5">
                      <span className="inline-block w-2.5 h-2.5 rounded-full mr-2" style={{ backgroundColor: NATION_COLORS[i % NATION_COLORS.length] }} />
                      {n.name}
                    </td>
                    <td className="py-1.5 text-right tabular-nums">{n.cities}</td>
                    <td className="py-1.5 text-right tabular-nums">{n.power}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={gameStats.nations} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis type="number" stroke="#9ca3af" />
                <YAxis type="category" dataKey="name" stroke="#9ca3af" width={80} tick={{ fontSize: 12 }} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', color: '#f3f4f6' }}
                />
                <Bar dataKey="cities" name="Cities" fill="#f59e0b" />
                <Bar dataKey="power" name="Power" fill="#3b82f6" />
                <Legend />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}

      {/* Time Series Charts */}
      {snapshots.length > 0 && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-3">Game Progress</h2>
          {renderTimeSeriesChart('Cities Over Time', 'cities')}
          {renderTimeSeriesChart('Power Over Time', 'power')}
          {renderTimeSeriesChart('Tech Over Time', 'tech')}
          {navalByNation.length > 0 && (
            <div className="mb-8">
              <h3 className="text-lg font-semibold mb-2">Naval Units Built Per Day</h3>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={navalByNation}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                  <XAxis dataKey="day" stroke="#9ca3af" label={{ value: 'Day', position: 'insideBottom', offset: -5, fill: '#9ca3af' }} />
                  <YAxis stroke="#9ca3af" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', color: '#f3f4f6' }}
                    labelFormatter={(label) => `Day ${label}`}
                  />
                  <Legend />
                  {navalNationNames.map((name, i) => (
                    <Line
                      key={name}
                      type="monotone"
                      dataKey={name}
                      stroke={NATION_COLORS[i % NATION_COLORS.length]}
                      dot={false}
                      strokeWidth={2}
                    />
                  ))}
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>
      )}

      {/* Unit Love */}
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

      {/* Player Unit Production */}
      {selectedGameId != null && players.length > 0 && (
        <>
          <div className="mb-4">
            <label className="text-sm text-gray-400 mr-2">Player:</label>
            <select
              data-testid="stats-player-select"
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

      {selectedGameId != null && unitLove.length === 0 && !gameStats && !loading && (
        <p className="text-gray-400">No data available for this game.</p>
      )}
    </div>
  )
}
