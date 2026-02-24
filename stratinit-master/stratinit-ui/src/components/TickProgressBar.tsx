import { useState, useEffect } from 'react'
import { useTick } from '../context/TickContext'

function formatCountdown(ms: number): string {
  if (ms <= 0) return '00:00'
  const totalSeconds = Math.ceil(ms / 1000)
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

export default function TickProgressBar() {
  const { tickInfo } = useTick()
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])

  if (!tickInfo) return null

  const lastUpdatedMs = new Date(tickInfo.lastUpdated).getTime()
  const elapsed = now - lastUpdatedMs
  const interval = tickInfo.tickIntervalMs
  // Handle wrapping: elapsed can exceed interval if server hasn't ticked yet
  const elapsedInTick = elapsed % interval
  const remaining = interval - elapsedInTick
  const progress = Math.min(elapsedInTick / interval, 1)

  return (
    <div className="flex items-center gap-2" data-testid="tick-progress-bar">
      <div className="w-24 h-2 bg-gray-700 rounded-full overflow-hidden">
        <div
          className="h-full bg-blue-500 rounded-full transition-[width] duration-1000 ease-linear"
          style={{ width: `${progress * 100}%` }}
        />
      </div>
      <span className="text-xs text-gray-400 font-mono tabular-nums w-12">
        {formatCountdown(remaining)}
      </span>
    </div>
  )
}
