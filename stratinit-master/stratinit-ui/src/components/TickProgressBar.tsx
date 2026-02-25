import { useState, useEffect } from 'react'
import { useTick } from '../context/TickContext'

function formatCountdown(ms: number): string {
  if (ms <= 0) return '00:00:00'
  const totalSeconds = Math.ceil(ms / 1000)
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

function formatEnds(dateStr: string): string {
  const d = new Date(dateStr)
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hours = d.getHours()
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const ampm = hours >= 12 ? 'PM' : 'AM'
  const h = hours % 12 || 12
  return `${month}/${day} ${h}:${minutes} ${ampm}`
}

export default function TickProgressBar() {
  const { tickInfo } = useTick()
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])

  if (!tickInfo) return null

  const game = tickInfo.game
  const gameEnded = game?.gameEnds ? new Date(game.gameEnds).getTime() <= now : false

  if (gameEnded) {
    return (
      <div className="flex items-center gap-2" data-testid="tick-progress-bar">
        {game && (
          <span className="text-xs text-gray-400">
            {game.gameName} #{game.gameId}
            <span className="text-red-400 font-semibold"> ended {formatEnds(game.gameEnds!)}</span>
          </span>
        )}
      </div>
    )
  }

  const lastUpdatedMs = new Date(tickInfo.lastUpdated).getTime()
  const elapsed = now - lastUpdatedMs
  const interval = tickInfo.tickIntervalMs
  // Handle wrapping: elapsed can exceed interval if server hasn't ticked yet
  const elapsedInTick = elapsed % interval
  const remaining = interval - elapsedInTick
  const progress = Math.min(elapsedInTick / interval, 1)

  return (
    <div className="flex items-center gap-2" data-testid="tick-progress-bar">
      {game && (
        <span className="text-xs text-gray-400">
          {game.gameName} #{game.gameId}
          {game.gameEnds && (
            <span className="text-gray-500"> ends {formatEnds(game.gameEnds)}</span>
          )}
        </span>
      )}
      <div className="w-24 h-2 bg-gray-700 rounded-full overflow-hidden">
        <div
          className="h-full bg-blue-500 rounded-full transition-[width] duration-1000 ease-linear"
          style={{ width: `${progress * 100}%` }}
        />
      </div>
      <span className="text-xs text-gray-400 font-mono tabular-nums w-16">
        {formatCountdown(remaining)}
      </span>
    </div>
  )
}
