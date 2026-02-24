import { useState, useEffect } from 'react'
import * as gameApi from '../../api/game'
import type { SINewsLogsDay } from '../../types/game'

export default function NewsTab() {
  const [days, setDays] = useState<SINewsLogsDay[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    gameApi.getNewsLogs().then(days => {
      if (cancelled) return
      setDays(days)
      setLoading(false)
    }).catch(() => {
      if (!cancelled) setLoading(false)
    })
    return () => { cancelled = true }
  }, [])

  if (loading) return <p className="text-xs text-gray-500">Loading news...</p>
  if (days.length === 0) return <p className="text-xs text-gray-500">No news.</p>

  return (
    <div data-testid="news-log" className="space-y-3">
      {days.map(day => {
        const entries: { category: string; text: string }[] = []

        for (const b of day.bulletins) {
          entries.push({ category: 'Bulletin', text: b.message })
        }
        for (const f of day.firsts) {
          entries.push({ category: 'First', text: `${f.nationName} built the first ${f.unitType}` })
        }
        for (const fa of day.foreignAffairs) {
          entries.push({
            category: 'Diplomacy',
            text: `${fa.nationName} changed relations with ${fa.opponentName}: ${fa.oldRelation} -> ${fa.newRelation}`,
          })
        }
        for (const nc of day.neutralConquests) {
          entries.push({
            category: 'Conquest',
            text: `${nc.nationName} conquered ${nc.count} neutral cit${nc.count === 1 ? 'y' : 'ies'}`,
          })
        }
        for (const oc of day.opponentConquest) {
          entries.push({
            category: 'Conquest',
            text: `${oc.nationName} conquered ${oc.count} cit${oc.count === 1 ? 'y' : 'ies'} from ${oc.opponentName}`,
          })
        }
        for (const nf of day.newsFromTheFront) {
          const verb = nf.killed ? 'destroyed' : 'damaged'
          entries.push({
            category: 'Combat',
            text: `${nf.nationName}'s ${nf.nationUnitType} ${verb} ${nf.opponentName}'s ${nf.opponentUnitType} (x${nf.count})`,
          })
        }
        for (const nd of day.nuclearDetonations) {
          entries.push({
            category: 'Nuclear',
            text: `${nd.nationName} launched ${nd.launchableUnit} at ${nd.opponentName} (x${nd.count})`,
          })
        }
        for (const ad of day.airDefense) {
          entries.push({
            category: 'Air Defense',
            text: `${ad.nationName}'s ${ad.nationUnitType} shot down by ${ad.opponentName}'s flak (x${ad.count})`,
          })
        }

        if (entries.length === 0) return null

        return (
          <div key={day.day}>
            <h3 className="font-bold text-gray-300 text-xs border-b border-gray-700 pb-0.5 mb-1">
              Day {day.day}
            </h3>
            <div className="space-y-1">
              {entries.map((e, i) => (
                <div key={i} className="text-xs">
                  <span className={`font-semibold ${categoryColor(e.category)}`}>
                    [{e.category}]
                  </span>{' '}
                  {e.text}
                </div>
              ))}
            </div>
          </div>
        )
      })}
    </div>
  )
}

function categoryColor(category: string): string {
  switch (category) {
    case 'Bulletin': return 'text-blue-400'
    case 'First': return 'text-green-400'
    case 'Diplomacy': return 'text-purple-400'
    case 'Conquest': return 'text-orange-400'
    case 'Combat': return 'text-red-400'
    case 'Nuclear': return 'text-yellow-400'
    case 'Air Defense': return 'text-cyan-400'
    default: return 'text-gray-400'
  }
}
