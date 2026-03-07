import type { UnitType, CityType } from './game'

// Unit type abbreviations (shared across components)
export const UNIT_SHORT: Record<UnitType, string> = {
  INFANTRY: 'In', TANK: 'Tk', PATROL: 'Pt', TRANSPORT: 'Tp', DESTROYER: 'DD',
  SUPPLY: 'Su', BATTLESHIP: 'BB', SUBMARINE: 'SS', CARRIER: 'CV', CRUISER: 'CA',
  FIGHTER: 'Fi', NAVAL_BOMBER: 'NB', HELICOPTER: 'He', HEAVY_BOMBER: 'HB',
  ZEPPELIN: 'Zp', SATELLITE: 'Sa', ICBM_1: 'M1', ICBM_2: 'M2', ICBM_3: 'M3',
  BASE: 'Ba', ENGINEER: 'En', CARGO_PLANE: 'CP', RESEARCH: 'Rs',
}

export function unitIconPath(type: UnitType, color?: string): string {
  const file = type.toLowerCase()
  return color ? `/images/unit/${color}/${file}.png` : `/images/unit/${file}.png`
}

export function cityIconPath(type: CityType, color?: string): string {
  const file = type.toLowerCase()
  return color ? `/images/city/${color}/${file}.png` : `/images/city/${file}.png`
}
