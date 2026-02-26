import { useState, useEffect } from 'react'
import { useTick, type PowerBreakdownEntry } from '../context/TickContext'

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

function fmt(n: number): string {
  return n % 1 === 0 ? n.toFixed(1) : n.toFixed(1)
}

function PowerWithTooltip({ power, powerLimit, powerColor, breakdown, cities }: {
  power: number
  powerLimit: number
  powerColor: string
  breakdown: PowerBreakdownEntry[]
  cities: number
}) {
  const [hover, setHover] = useState(false)
  const base = 0.5
  const unitTotal = breakdown.reduce((sum, e) => sum + e.count * (e.prodTime / 10), 0)
  const total = base + unitTotal

  return (
    <span
      className={`text-xs ${powerColor} relative cursor-default`}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
    >
      Pwr: {power}/{powerLimit}
      {hover && (
        <div className="absolute top-full left-0 mt-1 z-50 bg-gray-900 border border-gray-700 rounded px-3 py-2 text-xs whitespace-nowrap shadow-lg font-mono text-gray-200">
          <table>
            <tbody>
              <tr>
                <td colSpan={3}>Base</td>
                <td className="pl-2 text-right">= {fmt(base)}</td>
              </tr>
              {breakdown.map(e => (
                <tr key={e.type}>
                  <td className="pr-1">{e.abbrev}</td>
                  <td className="text-right pr-1">{e.count}</td>
                  <td>&times; {fmt(e.prodTime / 10)}</td>
                  <td className="pl-2 text-right">= {fmt(e.count * (e.prodTime / 10))}</td>
                </tr>
              ))}
              <tr className="border-t border-gray-700">
                <td colSpan={3} className="pt-1">Total</td>
                <td className="pl-2 text-right pt-1">= {fmt(total)}</td>
              </tr>
              <tr>
                <td colSpan={3} className="pt-1 text-gray-400">Limit ({cities} {cities === 1 ? 'city' : 'cities'} &times; 5)</td>
                <td className="pl-2 text-right pt-1 text-gray-400">= {powerLimit}</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </span>
  )
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
            <PowerWithTooltip
              power={game.power}
              powerLimit={game.powerLimit}
              powerColor={powerColor}
              breakdown={game.powerBreakdown}
              cities={game.cities}
            />
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
          <PowerWithTooltip
            power={game.power}
            powerLimit={game.powerLimit}
            powerColor={powerColor}
            breakdown={game.powerBreakdown}
            cities={game.cities}
          />
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
