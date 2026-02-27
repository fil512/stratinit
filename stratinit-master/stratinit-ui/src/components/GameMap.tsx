import { useRef, useEffect, useCallback, useState } from 'react'
import { useGame } from '../context/GameContext'
import type { RelationType, UnitType, CityType, SISector, SIUnit, SIUnitBase, SICityUpdate } from '../types/game'
import { UNIT_SHORT } from '../types/units'

// Zoom bounds and animation
const MIN_CELL = 8
const MAX_CELL = 48
const ZOOM_FACTOR = 1.2      // multiplier per wheel notch
const LERP_SPEED = 0.18      // per-frame exponential lerp (0–1, higher = faster)
const SNAP_THRESHOLD = 0.003 // fraction of target to snap
const DRAG_THRESHOLD = 4
const SQRT3 = Math.sqrt(3)

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

function hpBarColor(ratio: number): string {
  if (ratio >= 0.8) return '#00ff00'
  if (ratio >= 0.5) return '#ffff00'
  if (ratio >= 0.3) return '#ff8800'
  return '#ff0000'
}

// ── Toroidal / hex geometry utilities ──

function wrapCoord(c: number, bs: number): number {
  return ((c % bs) + bs) % bs
}

// World size in pixels for the full hex grid
function worldSize(bs: number, r: number): { w: number; h: number } {
  return {
    w: r + bs * 1.5 * r + 0.5 * r,
    h: SQRT3 * r * bs + SQRT3 / 2 * r,
  }
}

// Pixel center of an *unwrapped* (col, row) in flat-top hex layout
// col/row are unwrapped indices (can be outside [0, bs)), not game coords
function unwrappedHexCenter(col: number, row: number, r: number): [number, number] {
  const px = r + col * 1.5 * r
  const py = SQRT3 / 2 * r + row * SQRT3 * r + (col & 1 ? SQRT3 / 2 * r : 0)
  return [px, py]
}

// Draw a flat-top hexagon path centered at (cx, cy) with radius r
function drawHex(ctx: CanvasRenderingContext2D, cx: number, cy: number, r: number) {
  ctx.beginPath()
  for (let i = 0; i < 6; i++) {
    const angle = Math.PI / 3 * i  // flat-top: starts at 0°
    const px = cx + r * Math.cos(angle)
    const py = cy + r * Math.sin(angle)
    i === 0 ? ctx.moveTo(px, py) : ctx.lineTo(px, py)
  }
  ctx.closePath()
}

// Convert game coords (gx, gy) to the unwrapped (col, row) used by the hex layout
// gy is game-Y (0=bottom), row is visual row (0=top)
function gameToColRow(gx: number, gy: number, bs: number): [number, number] {
  return [gx, bs - 1 - gy]
}

// ── Hex distance (even-Q flat-top, toroidal) ──

// Convert odd-Q offset to cube coords (odd columns are shifted down)
function oddQToCube(col: number, row: number): [number, number, number] {
  const q = col
  const r = row - Math.floor(col / 2)
  const s = -q - r
  return [q, r, s]
}

// Hex distance on a toroidal even-Q grid
function hexDistWrapped(gx1: number, gy1: number, gx2: number, gy2: number, bs: number): number {
  // Convert game coords to col/row
  const [col1, row1] = gameToColRow(gx1, gy1, bs)
  const [col2, row2] = gameToColRow(gx2, gy2, bs)
  // Check all 9 toroidal copies and return minimum distance
  let best = Infinity
  for (let dx = -1; dx <= 1; dx++) {
    for (let dy = -1; dy <= 1; dy++) {
      const c2 = col2 + dx * bs
      const r2 = row2 + dy * bs
      const [q1, r1s, s1] = oddQToCube(col1, row1)
      const [q2, r2s, s2] = oddQToCube(c2, r2)
      const d = Math.max(Math.abs(q1 - q2), Math.abs(r1s - r2s), Math.abs(s1 - s2))
      if (d < best) best = d
    }
  }
  return best
}

// ── Imperative draw function ──

interface DrawParams {
  canvas: HTMLCanvasElement
  cellSize: number
  boardSize: number
  camera: { x: number; y: number }
  viewport: { w: number; h: number }
  update: import('../types/game').SIUpdate
  lookups: { sectorMap: Map<string, SISector>; unitMap: Map<string, SIUnit[]>; cityMap: Map<string, SICityUpdate> }
  selectedCoords: import('../types/game').SectorCoords | null
  selectedUnitIds: Set<number>
  unitBaseMap: Map<UnitType, SIUnitBase>
}

function drawMap(p: DrawParams) {
  const { canvas, cellSize: cs, boardSize: bs, camera, viewport, update, lookups, selectedCoords, selectedUnitIds, unitBaseMap } = p
  const r = cs / 2
  // Use raw camera directly — do NOT wrap. Wrapping by one world-width flips
  // column parity when boardSize is odd, causing hex vertical offsets to shift.
  const cam = camera

  // Resize canvas buffer for device pixel ratio (crisp rendering on HiDPI/Retina)
  const dpr = window.devicePixelRatio || 1
  const bufW = Math.round(viewport.w * dpr)
  const bufH = Math.round(viewport.h * dpr)
  if (canvas.width !== bufW || canvas.height !== bufH) {
    canvas.width = bufW
    canvas.height = bufH
  }

  const ctx = canvas.getContext('2d')
  if (!ctx) return
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  ctx.clearRect(0, 0, viewport.w, viewport.h)

  // Fog-of-war background
  ctx.fillStyle = TERRAIN_COLORS.UNKNOWN
  ctx.fillRect(0, 0, viewport.w, viewport.h)

  // Compute visible column/row range from camera + viewport
  // col range: pixel x range is [cam.x, cam.x + viewport.w]
  // For hex flat-top: px = r + col * 1.5 * r => col = (px - r) / (1.5 * r)
  const colMin = Math.floor((cam.x - r) / (1.5 * r)) - 1
  const colMax = Math.ceil((cam.x + viewport.w + r) / (1.5 * r)) + 1
  const rowMin = Math.floor((cam.y - SQRT3 * r) / (SQRT3 * r)) - 1
  const rowMax = Math.ceil((cam.y + viewport.h + SQRT3 * r) / (SQRT3 * r)) + 1

  // Helper: get screen position from unwrapped col, row
  const screenPos = (col: number, row: number): [number, number] => {
    const [wx, wy] = unwrappedHexCenter(col, row, r)
    return [wx - cam.x, wy - cam.y]
  }

  // Helper: lookup sector for wrapped coordinates
  const getSector = (col: number, row: number): SISector | undefined => {
    const gx = wrapCoord(col, bs)
    const gy = bs - 1 - wrapCoord(row, bs)
    return lookups.sectorMap.get(`${gx},${gy}`)
  }

  // Layer 1: Terrain (slight overlap to eliminate anti-aliasing seams)
  const terrainR = r + 0.5
  for (let col = colMin; col <= colMax; col++) {
    for (let row = rowMin; row <= rowMax; row++) {
      const sector = getSector(col, row)
      if (!sector) continue
      const [cx, cy] = screenPos(col, row)
      ctx.fillStyle = TERRAIN_COLORS[sector.type] ?? '#111'
      drawHex(ctx, cx, cy, terrainR)
      ctx.fill()
    }
  }

  // Grid lines at 24px+
  if (cs >= 24) {
    ctx.strokeStyle = 'rgba(255,255,255,0.08)'
    ctx.lineWidth = 0.5
    for (let col = colMin; col <= colMax; col++) {
      for (let row = rowMin; row <= rowMax; row++) {
        const sector = getSector(col, row)
        if (!sector) continue
        const [cx, cy] = screenPos(col, row)
        drawHex(ctx, cx, cy, r)
        ctx.stroke()
      }
    }
  }

  // Layer 2: Cities
  for (let col = colMin; col <= colMax; col++) {
    for (let row = rowMin; row <= rowMax; row++) {
      const sector = getSector(col, row)
      if (!sector?.cityType) continue
      const [cx, cy] = screenPos(col, row)
      ctx.fillStyle = relationColor(sector.myRelation)
      const insetR = r * 0.8
      drawHex(ctx, cx, cy, insetR)
      ctx.fill()
      drawCityDetails(ctx, sector, cx, cy, cs, r, lookups)
    }
  }

  // Layer 3: Units
  for (let col = colMin; col <= colMax; col++) {
    for (let row = rowMin; row <= rowMax; row++) {
      const sector = getSector(col, row)
      if (!sector?.topUnitType) continue
      const [cx, cy] = screenPos(col, row)
      ctx.fillStyle = relationColor(sector.myRelation)
      const insetR = r * 0.6
      drawHex(ctx, cx, cy, insetR)
      ctx.fill()
      const gx = wrapCoord(col, bs)
      const gy = bs - 1 - wrapCoord(row, bs)
      drawUnitDetails(ctx, sector, cx, cy, cs, r, { unitMap: lookups.unitMap }, unitBaseMap, gx, gy)
    }
  }

  // Supply lines — dotted lines from each of my units to nearest supply source
  const outOfSupplyCoords = new Set<string>()
  if (cs >= 16) {
    const SUPPLY_RADIUS = 6
    // Collect supply source game coords
    const landSupplySources: [number, number][] = []
    const navySupplySources: [number, number][] = []
    for (const [key, sector] of lookups.sectorMap) {
      if (sector.suppliesLand || sector.suppliesNavy) {
        const [gx, gy] = key.split(',').map(Number)
        if (sector.suppliesLand) landSupplySources.push([gx, gy])
        if (sector.suppliesNavy) navySupplySources.push([gx, gy])
      }
    }

    // Helper to draw a dotted supply line between two game coords
    const drawSupplyLine = (fromGx: number, fromGy: number, toGx: number, toGy: number) => {
      const [fromCol, fromRow] = gameToColRow(fromGx, fromGy, bs)
      const [toCol, toRow] = gameToColRow(toGx, toGy, bs)

      const nMin = Math.floor((colMin - fromCol) / bs) - 1
      const nMax = Math.ceil((colMax - fromCol) / bs) + 1
      const mMin = Math.floor((rowMin - fromRow) / bs) - 1
      const mMax = Math.ceil((rowMax - fromRow) / bs) + 1

      for (let n = nMin; n <= nMax; n++) {
        for (let m = mMin; m <= mMax; m++) {
          const srcCol = fromCol + n * bs
          const srcRow = fromRow + m * bs
          const [srcX, srcY] = screenPos(srcCol, srcRow)
          if (srcX < -cs * 2 || srcX > viewport.w + cs * 2 || srcY < -cs * 2 || srcY > viewport.h + cs * 2) continue

          // Find nearest destination copy (check 9 toroidal offsets)
          let bestDstCol = toCol + n * bs, bestDstRow = toRow + m * bs, bestPixDist = Infinity
          for (let ox = -1; ox <= 1; ox++) {
            for (let oy = -1; oy <= 1; oy++) {
              const dc = toCol + (n + ox) * bs
              const dr = toRow + (m + oy) * bs
              const dx = dc - srcCol, dy = dr - srcRow
              const dist = dx * dx + dy * dy
              if (dist < bestPixDist) { bestPixDist = dist; bestDstCol = dc; bestDstRow = dr }
            }
          }

          const [dstX, dstY] = screenPos(bestDstCol, bestDstRow)
          ctx.beginPath(); ctx.moveTo(srcX, srcY); ctx.lineTo(dstX, dstY); ctx.stroke()
        }
      }
    }

    ctx.save()
    ctx.setLineDash([Math.max(2, cs * 0.1), Math.max(2, cs * 0.1)])
    ctx.lineWidth = Math.max(0.5, cs * 0.03)

    // Draw supply lines from units that require supply to their nearest source
    for (const unit of update.units) {
      if (unit.nationId !== update.nationId) continue
      const ub = unitBaseMap.get(unit.type)
      if (!ub || !ub.requiresSupply) continue
      // Air units use fuel, not ground supply
      if (ub.requiresFuel) continue

      // Pick the right supply sources for this unit category
      const sources = ub.builtIn === 'PORT' ? navySupplySources : landSupplySources
      if (sources.length === 0) {
        outOfSupplyCoords.add(`${unit.coords.x},${unit.coords.y}`)
        continue
      }

      // Find nearest supply source within SUPPLY_RADIUS
      let bestSrc: [number, number] | null = null
      let bestDist = Infinity
      for (const [sx, sy] of sources) {
        const d = hexDistWrapped(unit.coords.x, unit.coords.y, sx, sy, bs)
        if (d > 0 && d <= SUPPLY_RADIUS && d < bestDist) {
          bestDist = d
          bestSrc = [sx, sy]
        }
      }
      if (!bestSrc) {
        outOfSupplyCoords.add(`${unit.coords.x},${unit.coords.y}`)
        continue
      }

      const isNavy = ub.builtIn === 'PORT'
      ctx.strokeStyle = isNavy ? 'rgba(0, 136, 255, 0.5)' : 'rgba(0, 255, 136, 0.5)'
      drawSupplyLine(unit.coords.x, unit.coords.y, bestSrc[0], bestSrc[1])
    }

    // Draw supply chain lines from supply-providing units to their nearest city/port
    const SUPPLY_UNIT_TYPES: Set<UnitType> = new Set(['SUPPLY' as UnitType, 'TRANSPORT' as UnitType, 'ENGINEER' as UnitType])
    for (const unit of update.units) {
      if (unit.nationId !== update.nationId) continue
      if (!SUPPLY_UNIT_TYPES.has(unit.type)) continue

      // Determine if this unit provides land or navy supply based on its sector
      const unitKey = `${unit.coords.x},${unit.coords.y}`
      const unitSector = lookups.sectorMap.get(unitKey)
      const isNavySupplier = unitSector?.suppliesNavy ?? false
      const isLandSupplier = unitSector?.suppliesLand ?? false
      if (!isNavySupplier && !isLandSupplier) continue

      // Find nearest supply source excluding own coords
      const sources = isNavySupplier ? navySupplySources : landSupplySources
      let bestSrc: [number, number] | null = null
      let bestDist = Infinity
      for (const [sx, sy] of sources) {
        if (sx === unit.coords.x && sy === unit.coords.y) continue
        const d = hexDistWrapped(unit.coords.x, unit.coords.y, sx, sy, bs)
        if (d > 0 && d <= SUPPLY_RADIUS && d < bestDist) {
          bestDist = d
          bestSrc = [sx, sy]
        }
      }
      if (!bestSrc) continue

      ctx.strokeStyle = isNavySupplier ? 'rgba(0, 136, 255, 0.5)' : 'rgba(0, 255, 136, 0.5)'
      drawSupplyLine(unit.coords.x, unit.coords.y, bestSrc[0], bestSrc[1])
    }

    ctx.restore()
  }

  // Red border on out-of-supply units
  if (outOfSupplyCoords.size > 0 && cs >= 16) {
    ctx.save()
    ctx.strokeStyle = 'rgba(255, 0, 0, 0.7)'
    ctx.lineWidth = 1.5
    for (let col = colMin; col <= colMax; col++) {
      for (let row = rowMin; row <= rowMax; row++) {
        const gx = wrapCoord(col, bs)
        const gy = bs - 1 - wrapCoord(row, bs)
        if (!outOfSupplyCoords.has(`${gx},${gy}`)) continue
        const [cx, cy] = screenPos(col, row)
        drawHex(ctx, cx, cy, r * 0.6)
        ctx.stroke()
      }
    }
    ctx.restore()
  }

  // Relation borders at 32px+
  if (cs >= 32) {
    for (let col = colMin; col <= colMax; col++) {
      for (let row = rowMin; row <= rowMax; row++) {
        const sector = getSector(col, row)
        if (!sector) continue
        if (!sector.myRelation || sector.myRelation === 'ME') continue
        if (sector.type === 'WATER' || sector.type === 'UNKNOWN' || sector.type === 'WASTELAND') continue
        if (!sector.cityType && !sector.topUnitType) continue
        const [cx, cy] = screenPos(col, row)
        ctx.strokeStyle = relationColor(sector.myRelation)
        ctx.lineWidth = 1.5
        drawHex(ctx, cx, cy, r * 0.95)
        ctx.stroke()
      }
    }
  }

  // Range overlays — fuel range and mobility range for selected unit
  if (selectedCoords && selectedUnitIds.size > 0) {
    const selectedUnitsAtCoords = (lookups.unitMap.get(`${selectedCoords.x},${selectedCoords.y}`) ?? [])
      .filter(u => selectedUnitIds.has(u.id))

    // Pick the first selected unit that has fuel or mobility to show ranges for
    const rangeUnit = selectedUnitsAtCoords[0]
    const rangeUnitBase = rangeUnit ? unitBaseMap.get(rangeUnit.type) : undefined

    if (rangeUnit && rangeUnitBase) {
      const hasFuel = rangeUnitBase.requiresFuel && rangeUnit.fuel > 0
      const fuelRange = hasFuel ? rangeUnit.fuel : 0
      const mobRange = rangeUnit.mobility
      const unitCategory = rangeUnitBase.builtIn // FORT=land, PORT=navy, AIRPORT=air, TECH=tech

      // Determine if hex is traversable by this unit
      const isTraversable = (gx: number, gy: number): boolean => {
        const sector = lookups.sectorMap.get(`${gx},${gy}`)
        const sType = sector?.type
        if (unitCategory === 'AIRPORT') return true // air units go anywhere
        if (unitCategory === 'FORT') {
          // Land units: land, cities, or fog-of-war (treated as land)
          return !sType || sType === 'UNKNOWN' || sType === 'LAND' || sType === 'PLAYER_CITY' || sType === 'NEUTRAL_CITY' || sType === 'START_CITY' || sType === 'WASTELAND'
        }
        if (unitCategory === 'PORT') {
          // Naval units: water, cities, or fog-of-war (treated as water)
          return !sType || sType === 'UNKNOWN' || sType === 'WATER' || sType === 'PLAYER_CITY' || sType === 'NEUTRAL_CITY' || sType === 'START_CITY'
        }
        return true // TECH or unknown category
      }

      // Fuel range overlay (outer ring)
      if (fuelRange > 0) {
        ctx.fillStyle = 'rgba(0, 180, 255, 0.15)'
        for (let col = colMin; col <= colMax; col++) {
          for (let row = rowMin; row <= rowMax; row++) {
            const gx = wrapCoord(col, bs)
            const gy = bs - 1 - wrapCoord(row, bs)
            const dist = hexDistWrapped(selectedCoords.x, selectedCoords.y, gx, gy, bs)
            if (dist > 0 && dist <= fuelRange) {
              const [cx, cy] = screenPos(col, row)
              drawHex(ctx, cx, cy, r)
              ctx.fill()
            }
          }
        }
      }

      // Mobility range overlay (inner ring, brighter)
      // Show for: fuel units (mobility within fuel range), and land/naval units
      const showMobility = hasFuel || unitCategory === 'FORT' || unitCategory === 'PORT'
      if (showMobility && mobRange > 0) {
        ctx.fillStyle = 'rgba(0, 255, 120, 0.18)'
        for (let col = colMin; col <= colMax; col++) {
          for (let row = rowMin; row <= rowMax; row++) {
            const gx = wrapCoord(col, bs)
            const gy = bs - 1 - wrapCoord(row, bs)
            const dist = hexDistWrapped(selectedCoords.x, selectedCoords.y, gx, gy, bs)
            if (dist > 0 && dist <= mobRange && isTraversable(gx, gy)) {
              const [cx, cy] = screenPos(col, row)
              drawHex(ctx, cx, cy, r)
              ctx.fill()
            }
          }
        }
      }
    }
  }

  // Selection highlight — draw at every visible wrapped copy
  if (selectedCoords) {
    for (let col = colMin; col <= colMax; col++) {
      for (let row = rowMin; row <= rowMax; row++) {
        const gx = wrapCoord(col, bs)
        const gy = bs - 1 - wrapCoord(row, bs)
        if (gx === selectedCoords.x && gy === selectedCoords.y) {
          const [cx, cy] = screenPos(col, row)
          ctx.strokeStyle = '#ffffff'
          ctx.lineWidth = 2
          drawHex(ctx, cx, cy, r)
          ctx.stroke()
        }
      }
    }
  }

  // Movement lines — draw for each visible copy of the unit's source hex
  ctx.lineWidth = cs >= 48 ? 2 : 1
  for (const unit of update.units) {
    if (!unit.nextCoords || unit.nationId !== update.nationId) continue
    const isSelected = selectedUnitIds.has(unit.id)
    ctx.strokeStyle = isSelected ? '#ffff00' : 'rgba(255,255,0,0.45)'

    const [fromCol, fromRow] = gameToColRow(unit.coords.x, unit.coords.y, bs)
    const [toCol, toRow] = gameToColRow(unit.nextCoords.x, unit.nextCoords.y, bs)

    // Enumerate visible copies of the source: shift by (n*bs, m*bs)
    const nMin = Math.floor((colMin - fromCol) / bs) - 1
    const nMax = Math.ceil((colMax - fromCol) / bs) + 1
    const mMin = Math.floor((rowMin - fromRow) / bs) - 1
    const mMax = Math.ceil((rowMax - fromRow) / bs) + 1

    for (let n = nMin; n <= nMax; n++) {
      for (let m = mMin; m <= mMax; m++) {
        const srcCol = fromCol + n * bs
        const srcRow = fromRow + m * bs
        const [srcX, srcY] = screenPos(srcCol, srcRow)
        if (srcX < -cs * 2 || srcX > viewport.w + cs * 2 || srcY < -cs * 2 || srcY > viewport.h + cs * 2) continue

        // Find nearest destination copy (check 9 toroidal offsets relative to this source)
        let bestDstCol = toCol + n * bs, bestDstRow = toRow + m * bs, bestDist = Infinity
        for (let ox = -1; ox <= 1; ox++) {
          for (let oy = -1; oy <= 1; oy++) {
            const dc = toCol + (n + ox) * bs
            const dr = toRow + (m + oy) * bs
            const dx = dc - srcCol
            const dy = dr - srcRow
            const dist = dx * dx + dy * dy
            if (dist < bestDist) { bestDist = dist; bestDstCol = dc; bestDstRow = dr }
          }
        }

        const [dstX, dstY] = screenPos(bestDstCol, bestDstRow)
        ctx.beginPath(); ctx.moveTo(srcX, srcY); ctx.lineTo(dstX, dstY); ctx.stroke()
        if (cs >= 48) {
          const angle = Math.atan2(dstY - srcY, dstX - srcX)
          const hl = 8
          ctx.beginPath()
          ctx.moveTo(dstX, dstY)
          ctx.lineTo(dstX - hl * Math.cos(angle - 0.4), dstY - hl * Math.sin(angle - 0.4))
          ctx.moveTo(dstX, dstY)
          ctx.lineTo(dstX - hl * Math.cos(angle + 0.4), dstY - hl * Math.sin(angle + 0.4))
          ctx.stroke()
        }
      }
    }
  }
}

// ── Component ──

export default function GameMap() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const { state, selectSectorFromMap, moveSelectedUnits } = useGame()
  const { boardSize, update, lookups, selectedCoords, selectedUnitIds, unitBases } = state

  // ── Camera state (world-pixel offset) ──
  const cameraRef = useRef({ x: 0, y: 0 })
  const viewportRef = useRef({ w: 800, h: 600 })
  const hasCenteredRef = useRef(false)

  // ── Zoom state ──
  const [cellSize, setCellSize] = useState(MIN_CELL)
  const currentCellRef = useRef(MIN_CELL)
  const targetCellRef = useRef(MIN_CELL)
  const animFrameRef = useRef(0)
  const focalRef = useRef<{ screenX: number; screenY: number } | null>(null)

  // Keep a ref to draw params so rAF can read latest without stale closures
  const drawParamsRef = useRef<Omit<DrawParams, 'canvas' | 'cellSize' | 'camera' | 'viewport'> | null>(null)
  const unitBaseMapRef = useRef(new Map<UnitType, SIUnitBase>())
  if (unitBases.length > 0 && unitBaseMapRef.current.size === 0) {
    for (const ub of unitBases) unitBaseMapRef.current.set(ub.type, ub)
  }
  if (update && lookups) {
    drawParamsRef.current = { boardSize, update, lookups, selectedCoords, selectedUnitIds, unitBaseMap: unitBaseMapRef.current }
  }

  // Drag-to-pan state
  const dragRef = useRef<{
    startX: number; startY: number
    cameraStartX: number; cameraStartY: number
    isDragging: boolean
  } | null>(null)

  // Pinch-to-zoom tracking
  const pointersRef = useRef<Map<number, { x: number; y: number }>>(new Map())
  const pinchStartDistRef = useRef<number | null>(null)
  const pinchStartCellRef = useRef(MIN_CELL)

  // ── rAF-coalesced redraw ──
  const redrawFrameRef = useRef(0)
  const requestRedraw = useCallback(() => {
    if (redrawFrameRef.current) return
    redrawFrameRef.current = requestAnimationFrame(() => {
      redrawFrameRef.current = 0
      const canvas = canvasRef.current
      const params = drawParamsRef.current
      if (!canvas || !params) return
      drawMap({
        canvas,
        cellSize: currentCellRef.current,
        camera: cameraRef.current,
        viewport: viewportRef.current,
        ...params,
      })
    })
  }, [])

  // ── Viewport size tracking via ResizeObserver ──
  useEffect(() => {
    const container = containerRef.current
    if (!container) return
    const ro = new ResizeObserver((entries) => {
      for (const entry of entries) {
        const { width, height } = entry.contentRect
        viewportRef.current = { w: Math.round(width), h: Math.round(height) }
        requestRedraw()
      }
    })
    ro.observe(container)
    // Initialize
    viewportRef.current = { w: container.clientWidth, h: container.clientHeight }
    return () => ro.disconnect()
  }, [requestRedraw])

  // ── Imperative zoom-frame: draw and adjust camera ──
  const applyZoomFrame = useCallback((cs: number) => {
    const canvas = canvasRef.current
    const params = drawParamsRef.current
    if (!canvas || !params) return

    const oldR = currentCellRef.current / 2
    const newR = cs / 2
    const oldWs = worldSize(params.boardSize, oldR)
    const newWs = worldSize(params.boardSize, newR)

    // Adjust camera to keep focal point fixed
    const focal = focalRef.current
    if (focal) {
      const cam = cameraRef.current
      // World position under focal point at old scale
      const worldX = cam.x + focal.screenX
      const worldY = cam.y + focal.screenY
      // Scale to new world size
      const scaleX = newWs.w / oldWs.w
      const scaleY = newWs.h / oldWs.h
      cameraRef.current = {
        x: worldX * scaleX - focal.screenX,
        y: worldY * scaleY - focal.screenY,
      }
    }

    currentCellRef.current = cs
    drawMap({
      canvas,
      cellSize: cs,
      camera: cameraRef.current,
      viewport: viewportRef.current,
      ...params,
    })
  }, [])

  // ── Animation loop ──
  const startAnimation = useCallback(() => {
    if (animFrameRef.current) return

    const animate = () => {
      const current = currentCellRef.current
      const target = targetCellRef.current

      if (Math.abs(current - target) / target < SNAP_THRESHOLD) {
        applyZoomFrame(target)
        setCellSize(target)
        animFrameRef.current = 0
        focalRef.current = null
        return
      }

      const logCurr = Math.log(current)
      const logTarg = Math.log(target)
      const next = Math.exp(logCurr + (logTarg - logCurr) * LERP_SPEED)
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

  // ── Redraw when game state changes ──
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !update || !lookups) return
    drawMap({
      canvas,
      cellSize: currentCellRef.current,
      boardSize,
      camera: cameraRef.current,
      viewport: viewportRef.current,
      update, lookups, selectedCoords, selectedUnitIds,
      unitBaseMap: unitBaseMapRef.current,
    })
  }, [update, lookups, selectedCoords, selectedUnitIds, boardSize])

  // Also redraw when cellSize settles
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !update || !lookups) return
    drawMap({
      canvas,
      cellSize,
      boardSize,
      camera: cameraRef.current,
      viewport: viewportRef.current,
      update, lookups, selectedCoords, selectedUnitIds,
      unitBaseMap: unitBaseMapRef.current,
    })
  }, [cellSize]) // eslint-disable-line react-hooks/exhaustive-deps

  // ── Auto-zoom to fit visible map on first load ──
  useEffect(() => {
    if (hasCenteredRef.current || !update || !lookups) return
    const vp = viewportRef.current
    if (vp.w === 0 || vp.h === 0) return

    // Find bounding box of all non-UNKNOWN sectors in col/row space
    let colMin = Infinity, colMax = -Infinity, rowMin = Infinity, rowMax = -Infinity
    let hasVisible = false
    for (const sector of update.sectors) {
      if (sector.type === 'UNKNOWN') continue
      const [col, row] = gameToColRow(sector.coords.x, sector.coords.y, boardSize)
      if (col < colMin) colMin = col
      if (col > colMax) colMax = col
      if (row < rowMin) rowMin = row
      if (row > rowMax) rowMax = row
      hasVisible = true
    }

    if (!hasVisible) return

    // Add padding of 1 hex on each side
    colMin -= 1
    colMax += 1
    rowMin -= 1
    rowMax += 1

    // Calculate cell size that fits this bounding box into the viewport
    // Width: cols span from colMin to colMax, pixel width = (colMax - colMin) * 1.5 * r + 2 * r
    // Height: rows span from rowMin to rowMax, pixel height = (rowMax - rowMin + 1) * sqrt3 * r
    const colSpan = colMax - colMin + 1
    const rowSpan = rowMax - rowMin + 1
    // Solve for r: vp.w = colSpan * 1.5 * r + 2 * r => r = vp.w / (colSpan * 1.5 + 2)
    const rFromWidth = vp.w / (colSpan * 1.5 + 2)
    // Solve for r: vp.h = rowSpan * sqrt3 * r + sqrt3 * r => r = vp.h / ((rowSpan + 1) * sqrt3)
    const rFromHeight = vp.h / ((rowSpan + 1) * SQRT3)
    const r = Math.min(rFromWidth, rFromHeight)
    const cs = Math.max(MIN_CELL, Math.min(MAX_CELL, r * 2))

    // Set zoom level
    currentCellRef.current = cs
    targetCellRef.current = cs
    setCellSize(cs)

    // Center camera on midpoint of bounding box
    const finalR = cs / 2
    const midCol = (colMin + colMax) / 2
    const midRow = (rowMin + rowMax) / 2
    const [worldPx, worldPy] = unwrappedHexCenter(midCol, midRow, finalR)
    cameraRef.current = { x: worldPx - vp.w / 2, y: worldPy - vp.h / 2 }

    hasCenteredRef.current = true
    requestRedraw()
  }, [update, lookups, boardSize, requestRedraw])

  // ── Scroll wheel zoom ──
  useEffect(() => {
    const container = containerRef.current
    if (!container) return

    const onWheel = (e: WheelEvent) => {
      e.preventDefault()
      let dy = e.deltaY
      if (e.deltaMode === 1) dy *= 40
      else if (e.deltaMode === 2) dy *= 800
      const steps = -dy / 100
      const factor = Math.pow(ZOOM_FACTOR, steps)
      zoomToward(targetCellRef.current * factor, e.clientX, e.clientY)
    }

    container.addEventListener('wheel', onWheel, { passive: false })
    return () => container.removeEventListener('wheel', onWheel)
  }, [zoomToward])

  // ── Click-drag panning (camera offset) ──
  useEffect(() => {
    const container = containerRef.current
    if (!container) return

    const onMouseDown = (e: MouseEvent) => {
      if (e.button !== 0) return
      if ((e.target as HTMLElement).closest('[data-zoom-btn]')) return
      dragRef.current = {
        startX: e.clientX, startY: e.clientY,
        cameraStartX: cameraRef.current.x, cameraStartY: cameraRef.current.y,
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
        cameraRef.current = {
          x: drag.cameraStartX - dx,
          y: drag.cameraStartY - dy,
        }
        requestRedraw()
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
  }, [requestRedraw])

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

  // ── Convert screen pixel → game coordinates (toroidal) ──
  const eventToGameCoords = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current
    if (!canvas || !boardSize) return null
    const rect = canvas.getBoundingClientRect()
    const r = currentCellRef.current / 2
    // Screen pixel → world pixel (add camera offset); use CSS pixels (not canvas buffer)
    // Use raw camera (no wrapping) to match drawMap's coordinate system
    const cam = cameraRef.current
    const worldX = (e.clientX - rect.left) + cam.x
    const worldY = (e.clientY - rect.top) + cam.y

    // Approximate column from world pixel
    const col = Math.round((worldX - r) / (1.5 * r))
    const rowOffset = (col & 1) ? SQRT3 / 2 * r : 0
    const adjustedPy = worldY - SQRT3 / 2 * r - rowOffset
    const row = Math.round(adjustedPy / (SQRT3 * r))

    // Check candidate and neighbors, find closest hex center
    let bestGx = 0, bestGy = 0, bestDist = Infinity
    for (let dc = -1; dc <= 1; dc++) {
      for (let dr = -1; dr <= 1; dr++) {
        const cc = col + dc
        const rr = row + dr
        const [hx, hy] = unwrappedHexCenter(cc, rr, r)
        const d = (worldX - hx) * (worldX - hx) + (worldY - hy) * (worldY - hy)
        if (d < bestDist) {
          bestDist = d
          bestGx = wrapCoord(cc, boardSize)
          bestGy = boardSize - 1 - wrapCoord(rr, boardSize)
        }
      }
    }

    return { x: bestGx, y: bestGy }
  }, [boardSize])

  // ── Left-click: move selected units to destination ──
  const handleClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (selectedUnitIds.size === 0) return
    const coords = eventToGameCoords(e)
    if (coords) moveSelectedUnits(coords)
  }, [eventToGameCoords, selectedUnitIds, moveSelectedUnits])

  // ── Right-click: select sector ──
  const handleContextMenu = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    e.preventDefault()
    const coords = eventToGameCoords(e)
    if (coords) selectSectorFromMap(coords)
  }, [eventToGameCoords, selectSectorFromMap])

  // ── Keyboard: +/- zoom, arrows pan ──
  const handleKeyDown = useCallback((e: React.KeyboardEvent) => {
    switch (e.key) {
      case '=': case '+': e.preventDefault(); zoomCenter(ZOOM_FACTOR); break
      case '-': e.preventDefault(); zoomCenter(1 / ZOOM_FACTOR); break
      case 'ArrowUp': e.preventDefault(); cameraRef.current = { ...cameraRef.current, y: cameraRef.current.y - (e.shiftKey ? 200 : 60) }; requestRedraw(); break
      case 'ArrowDown': e.preventDefault(); cameraRef.current = { ...cameraRef.current, y: cameraRef.current.y + (e.shiftKey ? 200 : 60) }; requestRedraw(); break
      case 'ArrowLeft': e.preventDefault(); cameraRef.current = { ...cameraRef.current, x: cameraRef.current.x - (e.shiftKey ? 200 : 60) }; requestRedraw(); break
      case 'ArrowRight': e.preventDefault(); cameraRef.current = { ...cameraRef.current, x: cameraRef.current.x + (e.shiftKey ? 200 : 60) }; requestRedraw(); break
    }
  }, [zoomCenter, requestRedraw])

  // ── Center on start location ──
  const centerOnStart = useCallback(() => {
    if (!update) return
    const myNation = update.nations.find(n => n.nationId === update.nationId)
    const coords = myNation?.startCoords
    if (!coords) return
    const cs = currentCellRef.current
    const r = cs / 2
    const [col, row] = gameToColRow(coords.x, coords.y, boardSize)
    const [worldPx, worldPy] = unwrappedHexCenter(col, row, r)
    const vp = viewportRef.current
    cameraRef.current = { x: worldPx - vp.w / 2, y: worldPy - vp.h / 2 }
    requestRedraw()
  }, [update, boardSize, requestRedraw])

  // Cleanup animation on unmount (reset refs so StrictMode remount works)
  useEffect(() => {
    return () => {
      if (animFrameRef.current) cancelAnimationFrame(animFrameRef.current)
      if (redrawFrameRef.current) cancelAnimationFrame(redrawFrameRef.current)
      animFrameRef.current = 0
      redrawFrameRef.current = 0
    }
  }, [])

  if (!update) return null

  return (
    <div className="relative h-full w-full">
      <div
        ref={containerRef}
        data-map-container
        className="h-full w-full border border-gray-600"
        style={{ touchAction: 'none', overflow: 'hidden' }}
        tabIndex={0}
        onKeyDown={handleKeyDown}
        onPointerDown={onPointerDown}
        onPointerMove={onPointerMove}
        onPointerUp={onPointerUp}
        onPointerCancel={onPointerUp}
      >
        <canvas
          ref={canvasRef}
          data-testid="game-map-canvas"
          onClick={handleClick}
          onContextMenu={handleContextMenu}
          onDoubleClick={handleDoubleClick}
          className="cursor-crosshair block w-full h-full"
        />
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
  cx: number, cy: number, cs: number, r: number,
  lookups: { cityMap: Map<string, SICityUpdate> },
) {
  if (!sector.cityType) return
  if (cs >= 16) {
    const label = CITY_SHORT[sector.cityType] ?? ''
    ctx.font = `bold ${Math.max(8, Math.round(cs * 0.4))}px monospace`
    ctx.fillStyle = '#000000'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'
    ctx.fillText(label, cx, cy)
  }
  if (cs >= 48) {
    const city = lookups.cityMap.get(`${sector.coords.x},${sector.coords.y}`)
    if (city && sector.myRelation === 'ME') {
      ctx.font = `${Math.max(7, Math.round(cs * 0.2))}px monospace`
      ctx.fillStyle = '#ffffff'; ctx.textAlign = 'center'; ctx.textBaseline = 'bottom'
      const b = city.build ? UNIT_SHORT[city.build] : '--'
      const n = city.nextBuild ? UNIT_SHORT[city.nextBuild] : '--'
      ctx.fillText(`${b}>${n}`, cx, cy + r * 0.8)
    }
  }
}

function drawUnitDetails(
  ctx: CanvasRenderingContext2D, sector: SISector,
  cx: number, cy: number, cs: number, r: number,
  lookups: { unitMap: Map<string, SIUnit[]> },
  unitBaseMap: Map<UnitType, SIUnitBase>,
  gx: number, gy: number,
) {
  if (!sector.topUnitType) return
  if (cs >= 16) {
    const label = UNIT_SHORT[sector.topUnitType] ?? ''
    ctx.font = `bold ${Math.max(7, Math.round(cs * 0.35))}px monospace`
    ctx.fillStyle = '#000000'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'
    ctx.fillText(label, cx, cy)
  }

  const units = lookups.unitMap.get(`${gx},${gy}`)
  const top = units?.[0]

  if (cs >= 24 && top) {
    const barW = r * 1.4, barH = Math.max(2, Math.round(cs * 0.1))
    const bx = cx - barW / 2, by = cy + r * 0.55
    const maxHp = unitBaseMap.get(top.type)?.hp ?? 10
    const hpRatio = Math.max(0, Math.min(1, top.hp / maxHp))
    ctx.fillStyle = 'rgba(0,0,0,0.5)'; ctx.fillRect(bx, by, barW, barH)
    ctx.fillStyle = hpBarColor(hpRatio); ctx.fillRect(bx, by, barW * hpRatio, barH)
  }

  if (cs >= 32 && top) {
    const fs = Math.max(7, Math.round(cs * 0.22))
    ctx.font = `${fs}px monospace`; ctx.textBaseline = 'top'
    ctx.fillStyle = '#ffffff'; ctx.textAlign = 'left'; ctx.fillText(`${top.hp}`, cx - r * 0.7, cy - r * 0.7)
    if (sector.flak > 0 || sector.cannons > 0) {
      ctx.textAlign = 'right'; ctx.fillStyle = '#ffaa00'
      ctx.fillText(sector.flak > 0 ? `F${sector.flak}` : `C${sector.cannons}`, cx + r * 0.7, cy - r * 0.7)
    }
    const ub = unitBaseMap.get(top.type)
    if (ub?.requiresFuel && top.fuel >= 0 && top.fuel <= 2) {
      ctx.fillStyle = '#ff4444'; ctx.textAlign = 'left'; ctx.textBaseline = 'bottom'
      ctx.fillText('!F', cx - r * 0.7, cy + r * 0.7)
    } else if (top.ammo === 0) {
      ctx.fillStyle = '#ff8800'; ctx.textAlign = 'left'; ctx.textBaseline = 'bottom'
      ctx.fillText('!A', cx - r * 0.7, cy + r * 0.7)
    }
  }

  if (cs >= 48 && top) {
    ctx.font = `${Math.max(7, Math.round(cs * 0.18))}px monospace`
    ctx.fillStyle = '#ffffff'; ctx.textAlign = 'center'; ctx.textBaseline = 'bottom'
    ctx.fillText(`${top.hp}/${top.ammo}/${top.fuel}/${top.mobility}`, cx, cy + r * 0.85)
  }
}
