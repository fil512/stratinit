import type { UnitType } from './game'

// Unit type abbreviations (shared across components)
export const UNIT_SHORT: Record<UnitType, string> = {
  INFANTRY: 'In', TANK: 'Tk', PATROL: 'Pt', TRANSPORT: 'Tp', DESTROYER: 'DD',
  SUPPLY: 'Su', BATTLESHIP: 'BB', SUBMARINE: 'SS', CARRIER: 'CV', CRUISER: 'CA',
  FIGHTER: 'Fi', NAVAL_BOMBER: 'NB', HELICOPTER: 'He', HEAVY_BOMBER: 'HB',
  ZEPPELIN: 'Zp', SATELLITE: 'Sa', ICBM_1: 'M1', ICBM_2: 'M2', ICBM_3: 'M3',
  BASE: 'Ba', ENGINEER: 'En', CARGO_PLANE: 'CP', RESEARCH: 'Rs',
}
