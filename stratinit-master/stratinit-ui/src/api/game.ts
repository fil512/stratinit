import { apiFetch } from './client'
import type {
  Result, SIUpdate, SIUnitBase, SIUnit, SICityUpdate, SIRelation,
  SectorCoords, RelationType, SIGame,
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
