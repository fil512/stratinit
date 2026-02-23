import { useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useGame } from '../context/GameContext'
import { useGameSocket, GameSocketMessage } from '../hooks/useGameSocket'
import { getJoinedGames } from '../api/game'
import GameMap from '../components/GameMap'
import SidePanel from '../components/SidePanel'

export default function GamePage() {
  const { gameId } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const { state, initGame, refreshState } = useGame()
  const gameIdNum = gameId ? parseInt(gameId, 10) : null

  useEffect(() => {
    if (!gameIdNum) {
      navigate('/games')
      return
    }
    // Fetch joined games to get boardSize, then init
    getJoinedGames().then(games => {
      const game = games.find(g => g.id === gameIdNum)
      if (!game) {
        navigate('/games')
        return
      }
      initGame(gameIdNum, game.size)
    }).catch(() => {
      navigate('/login')
    })
  }, [gameIdNum, navigate, initGame])

  const onSocketMessage = useCallback((_msg: GameSocketMessage) => {
    refreshState()
  }, [refreshState])

  useGameSocket(gameIdNum, onSocketMessage)

  if (state.loading) {
    return <div className="flex items-center justify-center h-full text-gray-400">Loading game...</div>
  }

  if (state.error) {
    return <div className="flex items-center justify-center h-full text-red-400">{state.error}</div>
  }

  if (!state.update) {
    return <div className="flex items-center justify-center h-full text-gray-400">Loading...</div>
  }

  return (
    <div className="flex h-full">
      <div className="flex-1 overflow-auto flex items-center justify-center p-2">
        <GameMap />
      </div>
      <SidePanel />
    </div>
  )
}
