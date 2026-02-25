import { apiFetch } from './client'
import type {
  SIUpdate, SIUnitBase, SIUnit, SICityUpdate, SIRelation,
  SectorCoords, RelationType, SIGame, SINation, SIMessage, SINewsLogsDay,
  SIPlayerRank, SITeamRank, SIUnitLove, SIUnitDayRow, SIGameHistory, SIGameStats,
  SINationSnapshot, SIGameEvent,
} from '../types/game'

export function setGame(gameId: number) {
  return apiFetch<void>('/stratinit/setGame', {
    method: 'POST',
    body: JSON.stringify({ gameId, noAlliances: false }),
  })
}

export function getUpdate() {
  return apiFetch<SIUpdate>('/stratinit/update')
}

export function getUnitBases() {
  return apiFetch<SIUnitBase[]>('/stratinit/unitbase')
}

export function getJoinedGames() {
  return apiFetch<SIGame[]>('/stratinit/joinedGames')
}

export function moveUnits(units: SIUnit[], target: SectorCoords) {
  return apiFetch<SIUpdate>('/stratinit/move-units', {
    method: 'POST',
    body: JSON.stringify({ units, target }),
  })
}

export function updateCity(sicity: SICityUpdate) {
  return apiFetch<SICityUpdate>('/stratinit/update-city', {
    method: 'POST',
    body: JSON.stringify({ sicity }),
  })
}

export function setRelation(nationId: number, relationType: RelationType) {
  return apiFetch<SIRelation>('/stratinit/set-relation', {
    method: 'POST',
    body: JSON.stringify({ nationId, relationType }),
  })
}

export function disbandUnits(siunits: SIUnit[]) {
  return apiFetch<SIUpdate>('/stratinit/disband-units', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function cancelMove(siunits: SIUnit[]) {
  return apiFetch<SIUpdate>('/stratinit/cancel-move', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function buildCity(siunits: SIUnit[]) {
  return apiFetch<SIUpdate>('/stratinit/build-city', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function switchTerrain(siunits: SIUnit[]) {
  return apiFetch<SIUpdate>('/stratinit/switch-terrain', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function cedeUnits(siunits: SIUnit[], nationId: number) {
  return apiFetch<SIUpdate>('/stratinit/cede-units', {
    method: 'POST',
    body: JSON.stringify({ siunits, nationId }),
  })
}

export function cedeCity(city: SICityUpdate, nationId: number) {
  return apiFetch<SIUpdate>('/stratinit/cede-city', {
    method: 'POST',
    body: JSON.stringify({ city, nationId }),
  })
}

export function getMail() {
  return apiFetch<SIMessage[]>('/stratinit/message-mail')
}

export function getSentMail() {
  return apiFetch<SIMessage[]>('/stratinit/message-sent')
}

export function getAnnouncements() {
  return apiFetch<SIMessage[]>('/stratinit/message-announcement')
}

export function sendMessage(message: { toNationId: number; subject: string; body: string }) {
  return apiFetch<number>('/stratinit/send-message', {
    method: 'POST',
    body: JSON.stringify(message),
  })
}

export function getNewsLogs() {
  return apiFetch<SINewsLogsDay[]>('/stratinit/newslog')
}

export function getUnjoinedGames() {
  return apiFetch<SIGame[]>('/stratinit/unjoinedGames')
}

export function joinGame(gameId: number, noAlliances: boolean) {
  return apiFetch<SINation>('/stratinit/joinGame', {
    method: 'POST',
    body: JSON.stringify({ gameId, noAlliances }),
  })
}

export function concede() {
  return apiFetch<SIUpdate>('/stratinit/concede')
}

export function createBlitzGame() {
  return apiFetch<SIGame>('/stratinit/create-blitz', {
    method: 'POST',
  })
}

export function addBot(gameId: number) {
  return apiFetch<SINation>('/stratinit/add-bot', {
    method: 'POST',
    body: JSON.stringify({ gameId, noAlliances: false }),
  })
}

export function getLeaderboard() {
  return apiFetch<SIPlayerRank[]>('/stratinit/leaderboard')
}

export function getTeamRankings() {
  return apiFetch<SITeamRank[]>('/stratinit/rankings/team')
}

export function getCompletedGames() {
  return apiFetch<SIGameHistory[]>('/stratinit/stats/games')
}

export function getGameDetail(gameId: number) {
  return apiFetch<SIGameStats>(`/stratinit/stats/game-detail?gameId=${gameId}`)
}

export function getGamePlayers(gameId: number) {
  return apiFetch<string[]>(`/stratinit/stats/players?gameId=${gameId}`)
}

export function getGameUnitLove(gameId: number) {
  return apiFetch<SIUnitLove[]>(`/stratinit/stats/game-units?gameId=${gameId}`)
}

export function getPlayerUnits(gameId: number, unitBaseType: string, username: string) {
  return apiFetch<SIUnitDayRow[]>(`/stratinit/stats/player-units?gameId=${gameId}&unitBaseType=${unitBaseType}&username=${encodeURIComponent(username)}`)
}

export function getGameTimeSeries(gameId: number) {
  return apiFetch<SINationSnapshot[]>(`/stratinit/stats/game-timeseries?gameId=${gameId}`)
}

export function getGameEvents(gameId: number, nationName?: string) {
  let url = `/stratinit/stats/game-events?gameId=${gameId}`
  if (nationName) url += `&nationName=${encodeURIComponent(nationName)}`
  return apiFetch<SIGameEvent[]>(url)
}
