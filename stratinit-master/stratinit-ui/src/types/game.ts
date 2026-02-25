// Enum types matching Java enums
export type SectorType = 'WATER' | 'LAND' | 'PLAYER_CITY' | 'NEUTRAL_CITY' | 'START_CITY' | 'WASTELAND' | 'UNKNOWN'

export type CityType = 'FORT' | 'PORT' | 'AIRPORT' | 'TECH' | 'BASE'

export type UnitType =
  | 'INFANTRY' | 'TANK' | 'PATROL' | 'TRANSPORT' | 'DESTROYER'
  | 'SUPPLY' | 'BATTLESHIP' | 'SUBMARINE' | 'CARRIER' | 'CRUISER'
  | 'FIGHTER' | 'NAVAL_BOMBER' | 'HELICOPTER' | 'HEAVY_BOMBER'
  | 'ZEPPELIN' | 'SATELLITE' | 'ICBM_1' | 'ICBM_2' | 'ICBM_3'
  | 'BASE' | 'ENGINEER' | 'CARGO_PLANE' | 'RESEARCH'

export type RelationType = 'WAR' | 'NEUTRAL' | 'FRIENDLY' | 'ALLIED' | 'ME'

export type CityFieldToUpdate = 'BUILD' | 'NEXT_BUILD' | 'SWITCH_ON_TECH_CHANGE' | 'NEXT_COORDS'

export type NewsCategory =
  | 'BULLETINS' | 'FOREIGN_AFFAIRS' | 'NUCLEAR_DETONATIONS'
  | 'CONQUEST' | 'NEWS_FROM_THE_FRONT' | 'AIR_DEFENCE' | 'FIRSTS'

// Core types
export interface SectorCoords {
  x: number
  y: number
}

export interface SISector {
  coords: SectorCoords
  type: SectorType
  cityType: CityType | null
  nationId: number
  lastSeen: string | null
  myRelation: RelationType | null
  theirRelation: RelationType | null
  holdsFriendlyCarrier: boolean
  holdsMyTransport: boolean
  suppliesLand: boolean
  suppliesNavy: boolean
  holdsMyCapital: boolean
  topUnitType: UnitType | null
  topUnitId: number
  flak: number
  cannons: number
  islandId: number
}

export interface SIUnit {
  id: number
  coords: SectorCoords
  nextCoords: SectorCoords | null
  nationId: number
  type: UnitType
  mobility: number
  hp: number
  ammo: number
  fuel: number
  lastUpdated: string | null
  created: string | null
}

export interface SINation {
  nationId: number
  name: string
  tech: number
  dailyTechGain: number
  dailyTechBleed: number
  hourlyCPGain: number
  cities: number
  lastAction: string | null
  wins: number
  played: number
  power: number
  newMail: boolean
  newBattle: boolean
  startCoords: SectorCoords | null
  commandPoints: number
  gameId: number
}

export interface SICityUpdate {
  coords: SectorCoords
  type: CityType
  field: CityFieldToUpdate | null
  build: UnitType | null
  nextBuild: UnitType | null
  nationId: number
  lastUpdated: string | null
  switchOnTechChange: boolean
  nextCoords: SectorCoords | null
}

export interface SIRelation {
  nationId: number
  meToThem: RelationType
  myNextType: RelationType
  mineSwitches: string | null
  themToMe: RelationType
  theirNextType: RelationType
  theirsSwitches: string | null
}

export interface SIBattleLog {
  id: number
  date: string
  coords: SectorCoords
  messages: string[]
  attackerId: number
  defenderId: number
  attackerUnit: string
  defenderUnit: string
  damage: number
  returnDamage: number
  attackerDied: boolean
  defenderDied: boolean
  type: NewsCategory
  flak: number
  iAmAttacker: boolean
}

export interface SILaunchedSatellite {
  id: number
  nationId: number
  coords: SectorCoords
}

export interface SIUpdate {
  nationId: number
  sectors: SISector[]
  units: SIUnit[]
  seenUnits: SIUnit[]
  nations: SINation[]
  cities: SICityUpdate[]
  log: SIBattleLog[]
  relations: SIRelation[]
  launchedSatellites: SILaunchedSatellite[]
  messages?: string[]
  lastUpdated: string | null
  tickIntervalMs: number
  gameId: number
  gameName: string
  gameEnds: string | null
  blitz: boolean
}

export interface SIUnitBase {
  builtIn: CityType
  type: UnitType
  attack: number
  hp: number
  mobility: number
  sight: number
  productionTime: number
  tech: number
  ammo: number
  flak: number
  lightAir: boolean
  canSeeSubs: boolean
  requiresFuel: boolean
  bombPercentage: number
  weight: number
  capacity: number
  navyCapital: boolean
  navyCanAttackLand: boolean
  landCanAttackShips: boolean
  launchable: boolean
  requiresSupply: boolean
  blastRadius: number
}

export interface SIGame {
  id: number
  name: string
  players: number
  noAlliancesVote: number
  noAlliances: boolean
  myNoAlliances: boolean
  islands: number
  size: number
  started: string | null
  mapped: string | null
  created: string | null
  ends: string | null
  blitz: boolean
}

// Messages
export interface SIMessage {
  messageId: number
  fromNationId: number
  toNationId: number
  subject: string
  body: string
  date: string | null
}

// News log types
export interface SINewsBulletin {
  category: NewsCategory
  message: string
}

export interface SINewsFirst {
  category: NewsCategory
  nationName: string
  unitType: UnitType
}

export interface SINewsForeignAffairs {
  category: NewsCategory
  nationName: string
  opponentName: string
  oldRelation: RelationType
  newRelation: RelationType
  effective: string | null
}

export interface SINewsNeutralConquest {
  category: NewsCategory
  nationName: string
  count: number
}

export interface SINewsOpponentConquest {
  category: NewsCategory
  nationName: string
  opponentName: string
  count: number
}

export interface SINewsFromTheFront {
  category: NewsCategory
  nationName: string
  opponentName: string
  count: number
  killed: boolean
  nationUnitType: UnitType
  opponentUnitType: UnitType
}

export interface SINewsNuclearDetonations {
  category: NewsCategory
  nationName: string
  opponentName: string
  count: number
  launchableUnit: UnitType
}

export interface SINewsAirDefense {
  category: NewsCategory
  nationName: string
  opponentName: string
  count: number
  nationUnitType: UnitType
}

// Rankings
export interface SIPlayerRank {
  username: string
  wins: number
  played: number
  winPercentage: number
}

export interface SITeamRank {
  name: string
  rank: number | null
  victories: number
  opponents: number
  wins: number
  played: number
  team: {
    nation1: string
    nation2: string | null
    score: number
    name: string
  }
}

// Unit statistics
export interface SIUnitLove {
  unitType: string
  love: number
}

export interface SIUnitDayRow {
  day: number
  counts: Record<string, number>
}

export interface SIGameHistory {
  gameId: number
  gamename: string
  gamesize: number
  startTime: string | null
  ends: string | null
}

export interface SIGameStats {
  gameId: number
  gamename: string
  gamesize: number
  startTime: string | null
  ends: string | null
  duration: number
  blitz: boolean
  nations: SINationStats[]
}

export interface SINationStats {
  name: string
  cities: number
  power: number
}

export interface SINewsLogsDay {
  day: number
  bulletins: SINewsBulletin[]
  firsts: SINewsFirst[]
  foreignAffairs: SINewsForeignAffairs[]
  neutralConquests: SINewsNeutralConquest[]
  nuclearDetonations: SINewsNuclearDetonations[]
  airDefense: SINewsAirDefense[]
  newsFromTheFront: SINewsFromTheFront[]
  opponentConquest: SINewsOpponentConquest[]
}
