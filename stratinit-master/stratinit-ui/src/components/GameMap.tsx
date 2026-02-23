import { useRef, useEffect, useCallback, useState } from 'react'
import { useGame } from '../context/GameContext'
import type { RelationType } from '../types/game'

const CELL_SIZE = 8

const TERRAIN_COLORS: Record<string, string> = {
  WATER: '#1a4b8c',
  LAND: '#2d6a1e',
  WASTELAND: '#6b6b6b',
  UNKNOWN: '#111111',
  PLAYER_CITY: '#2d6a1e',
  NEUTRAL_CITY: '#2d6a1e',
  START_CITY: '#2d6a1e',
}

const RELATION_COLORS: Record<string, string> = {
  ME: '#00e5ff',
  WAR: '#ff2222',
  FRIENDLY: '#ffdd00',
  ALLIED: '#ffa500',
  NEUTRAL: '#c0c0c0',
}

function relationColor(relation: RelationType | null): string {
  return relation ? (RELATION_COLORS[relation] ?? '#c0c0c0') : '#c0c0c0'
}

export default function GameMap() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const [hasScrolled, setHasScrolled] = useState(false)
  const { state, selectSector, moveSelectedUnits } = useGame()
  const { boardSize, update, lookups, selectedCoords, selectedUnitIds } = state

  const canvasSize = boardSize * CELL_SIZE

  // Convert game Y to canvas Y (invert Y axis)
  const toCanvasY = useCallback((gameY: number) => {
    return (boardSize - 1 - gameY) * CELL_SIZE
  }, [boardSize])

  // Draw the map
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !update || !lookups) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    ctx.clearRect(0, 0, canvasSize, canvasSize)

    // Layer 0: Fill unknown/fog-of-war background
    ctx.fillStyle = TERRAIN_COLORS.UNKNOWN
    ctx.fillRect(0, 0, canvasSize, canvasSize)

    // Layer 1: Terrain
    for (const sector of update.sectors) {
      const x = sector.coords.x * CELL_SIZE
      const y = toCanvasY(sector.coords.y)
      ctx.fillStyle = TERRAIN_COLORS[sector.type] ?? '#111'
      ctx.fillRect(x, y, CELL_SIZE, CELL_SIZE)
    }

    // Layer 2: Cities
    for (const sector of update.sectors) {
      if (!sector.cityType) continue
      const x = sector.coords.x * CELL_SIZE
      const y = toCanvasY(sector.coords.y)
      const color = relationColor(sector.myRelation)
      ctx.fillStyle = color
      ctx.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2)
    }

    // Layer 3: Units (top unit marker)
    for (const sector of update.sectors) {
      if (!sector.topUnitType) continue
      const x = sector.coords.x * CELL_SIZE
      const y = toCanvasY(sector.coords.y)
      const color = relationColor(sector.myRelation)
      ctx.fillStyle = color
      ctx.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4)
    }

    // Layer 4: Selection highlight
    if (selectedCoords) {
      const x = selectedCoords.x * CELL_SIZE
      const y = toCanvasY(selectedCoords.y)
      ctx.strokeStyle = '#ffffff'
      ctx.lineWidth = 2
      ctx.strokeRect(x, y, CELL_SIZE, CELL_SIZE)
    }

    // Layer 5: Movement lines for selected units
    if (selectedUnitIds.size > 0 && update) {
      ctx.strokeStyle = '#ffff00'
      ctx.lineWidth = 1
      for (const unit of update.units) {
        if (!selectedUnitIds.has(unit.id) || !unit.nextCoords) continue
        const fromX = unit.coords.x * CELL_SIZE + CELL_SIZE / 2
        const fromY = toCanvasY(unit.coords.y) + CELL_SIZE / 2
        const toX = unit.nextCoords.x * CELL_SIZE + CELL_SIZE / 2
        const toY = toCanvasY(unit.nextCoords.y) + CELL_SIZE / 2
        ctx.beginPath()
        ctx.moveTo(fromX, fromY)
        ctx.lineTo(toX, toY)
        ctx.stroke()
      }
    }
  }, [update, lookups, selectedCoords, selectedUnitIds, boardSize, canvasSize, toCanvasY])

  // Auto-scroll to center on player's units on first render
  useEffect(() => {
    if (hasScrolled || !update || !containerRef.current) return
    const myUnits = update.units.filter(u => u.nationId === update.nationId)
    if (myUnits.length === 0) return

    // Find center of player's units
    const avgX = myUnits.reduce((sum, u) => sum + u.coords.x, 0) / myUnits.length
    const avgY = myUnits.reduce((sum, u) => sum + u.coords.y, 0) / myUnits.length

    const canvasX = avgX * CELL_SIZE + CELL_SIZE / 2
    const canvasY = toCanvasY(avgY) + CELL_SIZE / 2

    const container = containerRef.current
    // Padding is canvasSize/2 on each side
    const pad = canvasSize / 2
    container.scrollLeft = pad + canvasX - container.clientWidth / 2
    container.scrollTop = pad + canvasY - container.clientHeight / 2
    setHasScrolled(true)
  }, [update, hasScrolled, toCanvasY])

  // Click handler
  const handleClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current
    if (!canvas || !boardSize) return

    const rect = canvas.getBoundingClientRect()
    const pixelX = e.clientX - rect.left
    const pixelY = e.clientY - rect.top

    const gameX = Math.floor(pixelX / CELL_SIZE)
    const gameY = boardSize - 1 - Math.floor(pixelY / CELL_SIZE)

    if (gameX < 0 || gameX >= boardSize || gameY < 0 || gameY >= boardSize) return

    if (selectedUnitIds.size > 0) {
      // Move selected units
      moveSelectedUnits({ x: gameX, y: gameY })
    } else {
      // Select sector
      selectSector({ x: gameX, y: gameY })
    }
  }, [boardSize, selectedUnitIds, moveSelectedUnits, selectSector])

  if (!update) return null

  // Half-canvas padding on all sides allows centering on any map position
  const pad = canvasSize / 2

  return (
    <div ref={containerRef} className="overflow-auto h-full w-full border border-gray-600">
      <div style={{ padding: pad, display: 'inline-block' }}>
        <canvas
          ref={canvasRef}
          width={canvasSize}
          height={canvasSize}
          onClick={handleClick}
          className="cursor-crosshair"
        />
      </div>
    </div>
  )
}
