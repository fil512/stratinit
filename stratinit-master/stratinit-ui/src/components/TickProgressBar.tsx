import { useState, useEffect } from 'react'
import { useTick } from '../context/TickContext'

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

  const powerColor = game && game.power >= game.powerLimit ? 'text-red-400' : 'text-gray-400'

  if (gameEnded) {
    return (
      <div className="flex items-center gap-2" data-testid="tick-progress-bar">
        {game && (
          <>
            <span className={`text-xs ${powerColor}`}>
              Pwr: {game.power}/{game.powerLimit}
            </span>
            <span className="text-xs text-gray-400">
              {game.gameName} #{game.gameId}
              <span className="text-red-400 font-semibold"> ended {formatEnds(game.gameEnds!)}</span>
            </span>
          </>
        )}
      </div>
    )
  }

  return (
    <div className="flex items-center gap-2" data-testid="tick-progress-bar">
      {game && (
        <>
          <span className={`text-xs ${powerColor}`}>
            Pwr: {game.power}/{game.powerLimit}
          </span>
          <span className="text-xs text-gray-400">
            {game.gameName} #{game.gameId}
            {game.gameEnds && (
              <span className="text-gray-500"> ends {formatEnds(game.gameEnds)}</span>
            )}
          </span>
        </>
      )}
    </div>
  )
}
