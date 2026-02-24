import { useRef, useEffect, useCallback, useState } from 'react'
import { useGame } from '../context/GameContext'
import type { RelationType, UnitType, CityType, SISector, SIUnit, SICityUpdate } from '../types/game'

// Zoom bounds and animation
const MIN_CELL = 8
const MAX_CELL = 48
const ZOOM_FACTOR = 1.2      // multiplier per wheel notch
const LERP_SPEED = 0.18      // per-frame exponential lerp (0–1, higher = faster)
const SNAP_THRESHOLD = 0.003 // fraction of target to snap
const DRAG_THRESHOLD = 4

// Unit type abbreviations
const UNIT_SHORT: Record<UnitType, string> = {
  INFANTRY: 'In', TANK: 'Tk', PATROL: 'Pt', TRANSPORT: 'Tp', DESTROYER: 'DD',
  SUPPLY: 'Su', BATTLESHIP: 'BB', SUBMARINE: 'SS', CARRIER: 'CV', CRUISER: 'CA',
  FIGHTER: 'Fi', NAVAL_BOMBER: 'NB', HELICOPTER: 'He', HEAVY_BOMBER: 'HB',
  ZEPPELIN: 'Zp', SATELLITE: 'Sa', ICBM_1: 'M1', ICBM_2: 'M2', ICBM_3: 'M3',
  BASE: 'Ba', ENGINEER: 'En', CARGO_PLANE: 'CP', RESEARCH: 'Rs',
}

const CITY_SHORT: Record<CityType, string> = {
  FORT: 'FT', PORT: 'PT', AIRPORT: 'AP', TECH: 'TC', BASE: 'BA',
}

const TERRAIN_COLORS: Record<string, string> = {
  WATER: '#1a4b8c', LAND: '#2d6a1e', WASTELAND: '#6b6b6b', UNKNOWN: '#111111',
  PLAYER_CITY: '#2d6a1e', NEUTRAL_CITY: '#2d6a1e', START_CITY: '#2d6a1e',
}

const RELATION_COLORS: Record<string, string> = {
  ME: '#00e5ff', WAR: '#ff2222', FRIENDLY: '#ffdd00', ALLIED: '#ffa500', NEUTRAL: '#c0c0c0',
}

function relationColor(relation: RelationType | null): string {
  return relation ? (RELATION_COLORS[relation] ?? '#c0c0c0') : '#c0c0c0'
}

function hpBarColor(hp: number): string {
  if (hp >= 8) return '#00ff00'
  if (hp >= 5) return '#ffff00'
  if (hp >= 3) return '#ff8800'
  return '#ff0000'
}

// ── Imperative draw function (used by both useEffect and rAF animation) ──

interface DrawParams {
  canvas: HTMLCanvasElement
  cellSize: number
  boardSize: number
  update: import('../types/game').SIUpdate
  lookups: { sectorMap: Map<string, SISector>; unitMap: Map<string, SIUnit[]>; cityMap: Map<string, SICityUpdate> }
  selectedCoords: import('../types/game').SectorCoords | null
  selectedUnitIds: Set<number>
}

function drawMap(p: DrawParams) {
  const { canvas, cellSize: cs, boardSize: bs, update, lookups, selectedCoords, selectedUnitIds } = p
  const size = bs * cs
  canvas.width = size
  canvas.height = size

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const toY = (gy: number) => (bs - 1 - gy) * cs

  // Layer 0: fog-of-war background
  ctx.fillStyle = TERRAIN_COLORS.UNKNOWN
  ctx.fillRect(0, 0, size, size)

  // Layer 1: Terrain
  for (const sector of update.sectors) {
    const x = sector.coords.x * cs
    const y = toY(sector.coords.y)
    ctx.fillStyle = TERRAIN_COLORS[sector.type] ?? '#111'
    ctx.fillRect(x, y, cs, cs)
  }

  // Grid lines at 24px+
  if (cs >= 24) {
    ctx.strokeStyle = 'rgba(255,255,255,0.08)'
    ctx.lineWidth = 0.5
    for (let i = 0; i <= bs; i++) {
      const pos = i * cs
      ctx.beginPath(); ctx.moveTo(pos, 0); ctx.lineTo(pos, size); ctx.stroke()
      ctx.beginPath(); ctx.moveTo(0, pos); ctx.lineTo(size, pos); ctx.stroke()
    }
  }

  // Layer 2: Cities
  for (const sector of update.sectors) {
    if (!sector.cityType) continue
    const x = sector.coords.x * cs
    const y = toY(sector.coords.y)
    ctx.fillStyle = relationColor(sector.myRelation)
    const inset = Math.max(1, Math.round(cs * 0.1))
    ctx.fillRect(x + inset, y + inset, cs - inset * 2, cs - inset * 2)
    drawCityDetails(ctx, sector, x, y, cs, lookups)
  }

  // Layer 3: Units
  for (const sector of update.sectors) {
    if (!sector.topUnitType) continue
    const x = sector.coords.x * cs
    const y = toY(sector.coords.y)
    ctx.fillStyle = relationColor(sector.myRelation)
    const inset = Math.max(2, Math.round(cs * 0.2))
    ctx.fillRect(x + inset, y + inset, cs - inset * 2, cs - inset * 2)
    drawUnitDetails(ctx, sector, x, y, cs, lookups)
  }

  // Supply dots at 24px+
  if (cs >= 24) {
    for (const sector of update.sectors) {
      if (!sector.suppliesLand && !sector.suppliesNavy) continue
      const x = sector.coords.x * cs
      const y = toY(sector.coords.y)
      const dotR = Math.max(1.5, cs * 0.06)
      if (sector.suppliesLand) {
        ctx.fillStyle = '#00ff88'
        ctx.beginPath(); ctx.arc(x + cs - dotR - 1, y + dotR + 1, dotR, 0, Math.PI * 2); ctx.fill()
      }
      if (sector.suppliesNavy) {
        ctx.fillStyle = '#0088ff'
        ctx.beginPath(); ctx.arc(x + cs - dotR - 1, y + dotR * 3 + 2, dotR, 0, Math.PI * 2); ctx.fill()
      }
    }
  }

  // Relation borders at 32px+
  if (cs >= 32) {
    for (const sector of update.sectors) {
      if (!sector.myRelation || sector.myRelation === 'ME') continue
      if (sector.type === 'WATER' || sector.type === 'UNKNOWN' || sector.type === 'WASTELAND') continue
      if (!sector.cityType && !sector.topUnitType) continue
      const x = sector.coords.x * cs
      const y = toY(sector.coords.y)
      ctx.strokeStyle = relationColor(sector.myRelation)
      ctx.lineWidth = 1.5
      ctx.strokeRect(x + 0.5, y + 0.5, cs - 1, cs - 1)
    }
  }

  // Selection highlight
  if (selectedCoords) {
    const x = selectedCoords.x * cs
    const y = toY(selectedCoords.y)
    ctx.strokeStyle = '#ffffff'
    ctx.lineWidth = 2
    ctx.strokeRect(x, y, cs, cs)
  }

  // Movement lines — always show for all player units with pending move orders
  ctx.lineWidth = cs >= 48 ? 2 : 1
  for (const unit of update.units) {
    if (!unit.nextCoords || unit.nationId !== update.nationId) continue
    const isSelected = selectedUnitIds.has(unit.id)
    ctx.strokeStyle = isSelected ? '#ffff00' : 'rgba(255,255,0,0.45)'
    const fromX = unit.coords.x * cs + cs / 2
    const fromY = toY(unit.coords.y) + cs / 2
    const toX = unit.nextCoords.x * cs + cs / 2
    const toYv = toY(unit.nextCoords.y) + cs / 2
    ctx.beginPath(); ctx.moveTo(fromX, fromY); ctx.lineTo(toX, toYv); ctx.stroke()
    if (cs >= 48) {
      const angle = Math.atan2(toYv - fromY, toX - fromX)
      const hl = 8
      ctx.beginPath()
      ctx.moveTo(toX, toYv)
      ctx.lineTo(toX - hl * Math.cos(angle - 0.4), toYv - hl * Math.sin(angle - 0.4))
      ctx.moveTo(toX, toYv)
      ctx.lineTo(toX - hl * Math.cos(angle + 0.4), toYv - hl * Math.sin(angle + 0.4))
      ctx.stroke()
    }
  }
}

// ── Component ──

export default function GameMap() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const paddingRef = useRef<HTMLDivElement>(null)
  const [hasScrolled, setHasScrolled] = useState(false)
  const { state, selectSector, moveSelectedUnits } = useGame()
  const { boardSize, update, lookups, selectedCoords, selectedUnitIds } = state

  // ── Zoom state ──
  // cellSize is React state (settled value, used for layout/clicks when not animating)
  const [cellSize, setCellSize] = useState(MIN_CELL)
  // Animated current and target tracked in refs for rAF loop
  const currentCellRef = useRef(MIN_CELL)
  const targetCellRef = useRef(MIN_CELL)
  const animFrameRef = useRef(0)
  // Focal point: screen-relative position that should stay fixed during zoom
  const focalRef = useRef<{ screenX: number; screenY: number } | null>(null)

  // Keep a ref to draw params so rAF can read latest without stale closures
  const drawParamsRef = useRef<Omit<DrawParams, 'canvas' | 'cellSize'> | null>(null)
  if (update && lookups) {
    drawParamsRef.current = { boardSize, update, lookups, selectedCoords, selectedUnitIds }
  }

  // Drag-to-pan state
  const dragRef = useRef<{
    startX: number; startY: number
    scrollStartX: number; scrollStartY: number
    isDragging: boolean
  } | null>(null)

  // Pinch-to-zoom tracking
  const pointersRef = useRef<Map<number, { x: number; y: number }>>(new Map())
  const pinchStartDistRef = useRef<number | null>(null)
  const pinchStartCellRef = useRef(MIN_CELL)

  // ── Imperative zoom-frame: resize, draw, adjust scroll ──
  const applyZoomFrame = useCallback((cs: number) => {
    const canvas = canvasRef.current
    const container = containerRef.current
    const paddingDiv = paddingRef.current
    const params = drawParamsRef.current
    if (!canvas || !container || !paddingDiv || !params) return

    const oldSize = canvas.width
    const newSize = params.boardSize * cs

    // Update padding (keeps canvas centerable at any scroll position)
    const pad = newSize / 2
    paddingDiv.style.padding = `${pad}px`

    // Draw at new size
    drawMap({ canvas, cellSize: cs, ...params })

    // Adjust scroll to keep focal point fixed
    const focal = focalRef.current
    if (focal && oldSize > 0) {
      const scale = newSize / oldSize
      const contentX = container.scrollLeft + focal.screenX
      const contentY = container.scrollTop + focal.screenY
      container.scrollLeft = contentX * scale - focal.screenX
      container.scrollTop = contentY * scale - focal.screenY
    }
  }, [])

  // ── Animation loop ──
  const startAnimation = useCallback(() => {
    if (animFrameRef.current) return // already running

    const animate = () => {
      const current = currentCellRef.current
      const target = targetCellRef.current

      if (Math.abs(current - target) / target < SNAP_THRESHOLD) {
        // Snap to target, stop animation
        currentCellRef.current = target
        applyZoomFrame(target)
        setCellSize(target)
        animFrameRef.current = 0
        focalRef.current = null
        return
      }

      // Exponential lerp in log space (multiplicative interpolation)
      const logCurr = Math.log(current)
      const logTarg = Math.log(target)
      const next = Math.exp(logCurr + (logTarg - logCurr) * LERP_SPEED)
      currentCellRef.current = next
      applyZoomFrame(next)

      animFrameRef.current = requestAnimationFrame(animate)
    }

    animFrameRef.current = requestAnimationFrame(animate)
  }, [applyZoomFrame])

  // ── Trigger zoom toward a screen point ──
  const zoomToward = useCallback((newTarget: number, screenX: number, screenY: number) => {
    const clamped = Math.max(MIN_CELL, Math.min(MAX_CELL, newTarget))
    if (clamped === targetCellRef.current) return
    const container = containerRef.current
    if (!container) return

    // Store focal point relative to container viewport
    const rect = container.getBoundingClientRect()
    focalRef.current = { screenX: screenX - rect.left, screenY: screenY - rect.top }
    targetCellRef.current = clamped
    startAnimation()
  }, [startAnimation])

  // ── Zoom toward viewport center (buttons/keyboard) ──
  const zoomCenter = useCallback((factor: number) => {
    const container = containerRef.current
    if (!container) return
    const rect = container.getBoundingClientRect()
    zoomToward(targetCellRef.current * factor, rect.left + rect.width / 2, rect.top + rect.height / 2)
  }, [zoomToward])

  // ── Redraw when game state changes (not zoom — zoom draws imperatively) ──
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !update || !lookups) return
    drawMap({ canvas, cellSize: currentCellRef.current, boardSize, update, lookups, selectedCoords, selectedUnitIds })
  }, [update, lookups, selectedCoords, selectedUnitIds, boardSize])

  // Also redraw when cellSize settles (animation end sets state)
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !update || !lookups) return
    drawMap({ canvas, cellSize, boardSize, update, lookups, selectedCoords, selectedUnitIds })
  }, [cellSize]) // eslint-disable-line react-hooks/exhaustive-deps

  // ── Auto-scroll to player's units on first load ──
  useEffect(() => {
    if (hasScrolled || !update || !containerRef.current) return
    const myUnits = update.units.filter(u => u.nationId === update.nationId)
    if (myUnits.length === 0) return

    const avgX = myUnits.reduce((sum, u) => sum + u.coords.x, 0) / myUnits.length
    const avgY = myUnits.reduce((sum, u) => sum + u.coords.y, 0) / myUnits.length

    const cs = currentCellRef.current
    const canvasX = avgX * cs + cs / 2
    const canvasY = (boardSize - 1 - avgY) * cs + cs / 2

    const container = containerRef.current
    const pad = boardSize * cs / 2
    container.scrollLeft = pad + canvasX - container.clientWidth / 2
    container.scrollTop = pad + canvasY - container.clientHeight / 2
    setHasScrolled(true)
  }, [update, hasScrolled, boardSize])

  // ── Scroll wheel zoom (greedy, no modifier) ──
  useEffect(() => {
    const container = containerRef.current
    if (!container) return

    const onWheel = (e: WheelEvent) => {
      e.preventDefault()
      // Normalize delta to pixels
      let dy = e.deltaY
      if (e.deltaMode === 1) dy *= 40
      else if (e.deltaMode === 2) dy *= 800
      // Each 100px of scroll = one ZOOM_FACTOR step
      const steps = -dy / 100
      const factor = Math.pow(ZOOM_FACTOR, steps)
      zoomToward(targetCellRef.current * factor, e.clientX, e.clientY)
    }

    container.addEventListener('wheel', onWheel, { passive: false })
    return () => container.removeEventListener('wheel', onWheel)
  }, [zoomToward])

  // ── Click-drag panning ──
  useEffect(() => {
    const container = containerRef.current
    if (!container) return

    const onMouseDown = (e: MouseEvent) => {
      if (e.button !== 0) return
      if ((e.target as HTMLElement).closest('[data-zoom-btn]')) return
      dragRef.current = {
        startX: e.clientX, startY: e.clientY,
        scrollStartX: container.scrollLeft, scrollStartY: container.scrollTop,
        isDragging: false,
      }
      container.style.cursor = 'grab'
    }

    const onMouseMove = (e: MouseEvent) => {
      const drag = dragRef.current
      if (!drag) return
      const dx = e.clientX - drag.startX
      const dy = e.clientY - drag.startY
      if (!drag.isDragging && Math.hypot(dx, dy) >= DRAG_THRESHOLD) {
        drag.isDragging = true
        container.style.cursor = 'grabbing'
      }
      if (drag.isDragging) {
        container.scrollLeft = drag.scrollStartX - dx
        container.scrollTop = drag.scrollStartY - dy
      }
    }

    const onMouseUp = (_e: MouseEvent) => {
      const drag = dragRef.current
      dragRef.current = null
      container.style.cursor = ''
      if (drag?.isDragging) {
        const suppress = (ce: MouseEvent) => { ce.stopPropagation(); ce.preventDefault() }
        container.addEventListener('click', suppress, { capture: true, once: true })
      }
    }

    container.addEventListener('mousedown', onMouseDown)
    window.addEventListener('mousemove', onMouseMove)
    window.addEventListener('mouseup', onMouseUp)
    return () => {
      container.removeEventListener('mousedown', onMouseDown)
      window.removeEventListener('mousemove', onMouseMove)
      window.removeEventListener('mouseup', onMouseUp)
    }
  }, [])

  // ── Double-click to zoom in ──
  const handleDoubleClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    e.preventDefault()
    zoomToward(targetCellRef.current * 2, e.clientX, e.clientY)
  }, [zoomToward])

  // ── Pinch-to-zoom (touch) ──
  const onPointerDown = useCallback((e: React.PointerEvent) => {
    if (e.pointerType !== 'touch') return
    pointersRef.current.set(e.pointerId, { x: e.clientX, y: e.clientY })
    if (pointersRef.current.size === 2) {
      const pts = Array.from(pointersRef.current.values())
      pinchStartDistRef.current = Math.hypot(pts[1].x - pts[0].x, pts[1].y - pts[0].y)
      pinchStartCellRef.current = targetCellRef.current
    }
  }, [])

  const onPointerMove = useCallback((e: React.PointerEvent) => {
    if (e.pointerType !== 'touch') return
    if (!pointersRef.current.has(e.pointerId)) return
    pointersRef.current.set(e.pointerId, { x: e.clientX, y: e.clientY })

    if (pointersRef.current.size === 2 && pinchStartDistRef.current !== null) {
      const pts = Array.from(pointersRef.current.values())
      const dist = Math.hypot(pts[1].x - pts[0].x, pts[1].y - pts[0].y)
      const ratio = dist / pinchStartDistRef.current
      const midX = (pts[0].x + pts[1].x) / 2
      const midY = (pts[0].y + pts[1].y) / 2
      zoomToward(pinchStartCellRef.current * ratio, midX, midY)
    }
  }, [zoomToward])

  const onPointerUp = useCallback((e: React.PointerEvent) => {
    pointersRef.current.delete(e.pointerId)
    if (pointersRef.current.size < 2) pinchStartDistRef.current = null
  }, [])

  // ── Helper: convert mouse event to game coordinates ──
  const eventToGameCoords = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current
    if (!canvas || !boardSize) return null
    const rect = canvas.getBoundingClientRect()
    const cs = currentCellRef.current
    const scaleX = canvas.width / rect.width
    const scaleY = canvas.height / rect.height
    const pixelX = (e.clientX - rect.left) * scaleX
    const pixelY = (e.clientY - rect.top) * scaleY
    const gameX = Math.floor(pixelX / cs)
    const gameY = boardSize - 1 - Math.floor(pixelY / cs)
    if (gameX < 0 || gameX >= boardSize || gameY < 0 || gameY >= boardSize) return null
    return { x: gameX, y: gameY }
  }, [boardSize])

  // ── Left-click: move selected units to destination ──
  const handleClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (selectedUnitIds.size === 0) return
    const coords = eventToGameCoords(e)
    if (coords) moveSelectedUnits(coords)
  }, [eventToGameCoords, selectedUnitIds, moveSelectedUnits])

  // ── Right-click: select sector (and auto-select units there) ──
  const handleContextMenu = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    e.preventDefault()
    const coords = eventToGameCoords(e)
    if (coords) selectSector(coords)
  }, [eventToGameCoords, selectSector])

  // ── Keyboard: +/- zoom, arrows pan ──
  const handleKeyDown = useCallback((e: React.KeyboardEvent) => {
    const container = containerRef.current
    if (!container) return
    switch (e.key) {
      case '=': case '+': e.preventDefault(); zoomCenter(ZOOM_FACTOR); break
      case '-': e.preventDefault(); zoomCenter(1 / ZOOM_FACTOR); break
      case 'ArrowUp': e.preventDefault(); container.scrollTop -= e.shiftKey ? 200 : 60; break
      case 'ArrowDown': e.preventDefault(); container.scrollTop += e.shiftKey ? 200 : 60; break
      case 'ArrowLeft': e.preventDefault(); container.scrollLeft -= e.shiftKey ? 200 : 60; break
      case 'ArrowRight': e.preventDefault(); container.scrollLeft += e.shiftKey ? 200 : 60; break
    }
  }, [zoomCenter])

  // ── Center on start location ──
  const centerOnStart = useCallback(() => {
    const container = containerRef.current
    if (!container || !update) return
    const myNation = update.nations.find(n => n.nationId === update.nationId)
    const coords = myNation?.startCoords
    if (!coords) return
    const cs = currentCellRef.current
    const canvasX = coords.x * cs + cs / 2
    const canvasY = (boardSize - 1 - coords.y) * cs + cs / 2
    const pad = boardSize * cs / 2
    container.scrollLeft = pad + canvasX - container.clientWidth / 2
    container.scrollTop = pad + canvasY - container.clientHeight / 2
  }, [update, boardSize])

  // Cleanup animation on unmount
  useEffect(() => {
    return () => { if (animFrameRef.current) cancelAnimationFrame(animFrameRef.current) }
  }, [])

  if (!update) return null

  const canvasSz = boardSize * cellSize
  const pad = canvasSz / 2

  return (
    <div className="relative h-full w-full">
      <div
        ref={containerRef}
        data-map-container
        className="overflow-auto h-full w-full border border-gray-600"
        style={{ touchAction: 'none', scrollbarWidth: 'none' }}
        tabIndex={0}
        onKeyDown={handleKeyDown}
        onPointerDown={onPointerDown}
        onPointerMove={onPointerMove}
        onPointerUp={onPointerUp}
        onPointerCancel={onPointerUp}
      >
        <style>{`[data-map-container]::-webkit-scrollbar { display: none; }`}</style>
        <div ref={paddingRef} style={{ padding: pad, display: 'inline-block' }}>
          <canvas
            ref={canvasRef}
            data-testid="game-map-canvas"
            width={canvasSz}
            height={canvasSz}
            onClick={handleClick}
            onContextMenu={handleContextMenu}
            onDoubleClick={handleDoubleClick}
            className="cursor-crosshair"
          />
        </div>
      </div>
      {/* Map controls */}
      <div className="absolute bottom-2 right-2 flex flex-col gap-1">
        <button
          data-zoom-btn
          data-testid="map-center-button"
          aria-label="Center map"
          onClick={centerOnStart}
          className="w-7 h-7 bg-white text-gray-600 border border-gray-300 rounded-sm hover:bg-gray-100 leading-none flex items-center justify-center shadow"
          title="Center on start"
        >
          <svg width="14" height="14" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
            <circle cx="8" cy="8" r="3" />
            <circle cx="8" cy="8" r="6.5" />
            <line x1="8" y1="0" x2="8" y2="3.5" />
            <line x1="8" y1="12.5" x2="8" y2="16" />
            <line x1="0" y1="8" x2="3.5" y2="8" />
            <line x1="12.5" y1="8" x2="16" y2="8" />
          </svg>
        </button>
        <div className="flex flex-col gap-0 shadow">
          <button
            data-zoom-btn
            data-testid="map-zoom-in-button"
            aria-label="Zoom in"
            onClick={() => zoomCenter(ZOOM_FACTOR)}
            className="w-7 h-7 bg-white text-gray-600 border border-gray-300 rounded-t-sm hover:bg-gray-100 text-base font-light leading-none"
            title="Zoom in"
          >+</button>
          <button
            data-zoom-btn
            data-testid="map-zoom-out-button"
            aria-label="Zoom out"
            onClick={() => zoomCenter(1 / ZOOM_FACTOR)}
            className="w-7 h-7 bg-white text-gray-600 border border-gray-300 border-t-0 rounded-b-sm hover:bg-gray-100 text-base font-light leading-none"
            title="Zoom out"
          >-</button>
        </div>
      </div>
    </div>
  )
}

// ── Detail rendering helpers ──

function drawCityDetails(
  ctx: CanvasRenderingContext2D, sector: SISector,
  x: number, y: number, cs: number,
  lookups: { cityMap: Map<string, SICityUpdate> },
) {
  if (!sector.cityType) return
  if (cs >= 16) {
    const label = CITY_SHORT[sector.cityType] ?? ''
    ctx.font = `bold ${Math.max(8, Math.round(cs * 0.4))}px monospace`
    ctx.fillStyle = '#000000'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'
    ctx.fillText(label, x + cs / 2, y + cs / 2)
  }
  if (cs >= 48) {
    const city = lookups.cityMap.get(`${sector.coords.x},${sector.coords.y}`)
    if (city && sector.myRelation === 'ME') {
      ctx.font = `${Math.max(7, Math.round(cs * 0.2))}px monospace`
      ctx.fillStyle = '#ffffff'; ctx.textAlign = 'center'; ctx.textBaseline = 'bottom'
      const b = city.build ? UNIT_SHORT[city.build] : '--'
      const n = city.nextBuild ? UNIT_SHORT[city.nextBuild] : '--'
      ctx.fillText(`${b}>${n}`, x + cs / 2, y + cs - 2)
    }
  }
}

function drawUnitDetails(
  ctx: CanvasRenderingContext2D, sector: SISector,
  x: number, y: number, cs: number,
  lookups: { unitMap: Map<string, SIUnit[]> },
) {
  if (!sector.topUnitType) return
  if (cs >= 16) {
    const label = UNIT_SHORT[sector.topUnitType] ?? ''
    ctx.font = `bold ${Math.max(7, Math.round(cs * 0.35))}px monospace`
    ctx.fillStyle = '#000000'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'
    ctx.fillText(label, x + cs / 2, y + cs / 2)
  }

  const units = lookups.unitMap.get(`${sector.coords.x},${sector.coords.y}`)
  const top = units?.[0]

  if (cs >= 24 && top) {
    const barW = cs - 6, barH = Math.max(2, Math.round(cs * 0.1))
    const bx = x + 3, by = y + cs - barH - 2
    ctx.fillStyle = 'rgba(0,0,0,0.5)'; ctx.fillRect(bx, by, barW, barH)
    ctx.fillStyle = hpBarColor(top.hp); ctx.fillRect(bx, by, barW * Math.max(0, Math.min(1, top.hp / 10)), barH)
  }

  if (cs >= 32 && top) {
    const fs = Math.max(7, Math.round(cs * 0.22))
    ctx.font = `${fs}px monospace`; ctx.textBaseline = 'top'
    ctx.fillStyle = '#ffffff'; ctx.textAlign = 'left'; ctx.fillText(`${top.hp}`, x + 2, y + 1)
    if (sector.flak > 0 || sector.cannons > 0) {
      ctx.textAlign = 'right'; ctx.fillStyle = '#ffaa00'
      ctx.fillText(sector.flak > 0 ? `F${sector.flak}` : `C${sector.cannons}`, x + cs - 2, y + 1)
    }
    if (top.fuel >= 0 && top.fuel <= 2) {
      ctx.fillStyle = '#ff4444'; ctx.textAlign = 'left'; ctx.textBaseline = 'bottom'
      ctx.fillText('!F', x + 2, y + cs - 2)
    } else if (top.ammo === 0) {
      ctx.fillStyle = '#ff8800'; ctx.textAlign = 'left'; ctx.textBaseline = 'bottom'
      ctx.fillText('!A', x + 2, y + cs - 2)
    }
  }

  if (cs >= 48 && top) {
    ctx.font = `${Math.max(7, Math.round(cs * 0.18))}px monospace`
    ctx.fillStyle = '#ffffff'; ctx.textAlign = 'center'; ctx.textBaseline = 'bottom'
    ctx.fillText(`${top.hp}/${top.ammo}/${top.fuel}/${top.mobility}`, x + cs / 2, y + cs - 2)
  }
}
