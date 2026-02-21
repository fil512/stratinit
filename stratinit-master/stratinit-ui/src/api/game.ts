import { apiFetch } from './client'
import type {
  Result, SIUpdate, SIUnitBase, SIUnit, SICityUpdate, SIRelation,
  SectorCoords, RelationType, SIGame, SIMessage, SINewsLogsDay,
} from '../types/game'

export function setGame(gameId: number) {
  return apiFetch<Result<unknown>>('/stratinit/setGame', {
    method: 'POST',
    body: JSON.stringify({ gameId, noAlliances: false }),
  })
}

export function getUpdate() {
  return apiFetch<Result<SIUpdate>>('/stratinit/update')
}

export function getUnitBases() {
  return apiFetch<Result<SIUnitBase[]>>('/stratinit/unitbase')
}

export function getJoinedGames() {
  return apiFetch<Result<SIGame[]>>('/stratinit/joinedGames')
}

export function moveUnits(units: SIUnit[], target: SectorCoords) {
  return apiFetch<Result<SIUpdate>>('/stratinit/move-units', {
    method: 'POST',
    body: JSON.stringify({ units, target }),
  })
}

export function updateCity(sicity: SICityUpdate) {
  return apiFetch<Result<SICityUpdate>>('/stratinit/update-city', {
    method: 'POST',
    body: JSON.stringify({ sicity }),
  })
}

export function setRelation(nationId: number, relationType: RelationType) {
  return apiFetch<Result<SIRelation>>('/stratinit/set-relation', {
    method: 'POST',
    body: JSON.stringify({ nationId, relationType }),
  })
}

export function disbandUnits(siunits: SIUnit[]) {
  return apiFetch<Result<SIUpdate>>('/stratinit/disband-units', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function cancelMove(siunits: SIUnit[]) {
  return apiFetch<Result<SIUpdate>>('/stratinit/cancel-move', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function buildCity(siunits: SIUnit[]) {
  return apiFetch<Result<SIUpdate>>('/stratinit/build-city', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function switchTerrain(siunits: SIUnit[]) {
  return apiFetch<Result<SIUpdate>>('/stratinit/switch-terrain', {
    method: 'POST',
    body: JSON.stringify({ siunits }),
  })
}

export function cedeUnits(siunits: SIUnit[], nationId: number) {
  return apiFetch<Result<SIUpdate>>('/stratinit/cede-units', {
    method: 'POST',
    body: JSON.stringify({ siunits, nationId }),
  })
}

export function cedeCity(city: SICityUpdate, nationId: number) {
  return apiFetch<Result<SIUpdate>>('/stratinit/cede-city', {
    method: 'POST',
    body: JSON.stringify({ city, nationId }),
  })
}

export function getMail() {
  return apiFetch<Result<SIMessage[]>>('/stratinit/message-mail')
}

export function getSentMail() {
  return apiFetch<Result<SIMessage[]>>('/stratinit/message-sent')
}

export function getAnnouncements() {
  return apiFetch<Result<SIMessage[]>>('/stratinit/message-announcement')
}

export function sendMessage(message: { toNationId: number; subject: string; body: string }) {
  return apiFetch<Result<number>>('/stratinit/send-message', {
    method: 'POST',
    body: JSON.stringify(message),
  })
}

export function getNewsLogs() {
  return apiFetch<Result<SINewsLogsDay[]>>('/stratinit/newslog')
}
