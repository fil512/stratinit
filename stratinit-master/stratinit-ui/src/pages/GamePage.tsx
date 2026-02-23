import { useEffect, useCallback, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useGame } from '../context/GameContext'
import { useGameSocket, GameSocketMessage } from '../hooks/useGameSocket'
import { getJoinedGames, addBot } from '../api/game'
import type { SIGame } from '../types/game'
import GameMap from '../components/GameMap'
import SidePanel from '../components/SidePanel'

function GameLobby({ game }: { game: SIGame }) {
  const [addingBot, setAddingBot] = useState(false)
  const status = game.mapped
    ? 'Game is mapped — starting soon...'
    : game.started
      ? 'World is being generated...'
      : 'Waiting for players to join...'

  const handleAddBot = () => {
    setAddingBot(true)
    addBot(game.id)
      .catch(() => {})
      .finally(() => setAddingBot(false))
  }

  return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center max-w-md">
        <h2 className="text-2xl font-bold mb-4">{game.name}</h2>
        <p className="text-gray-400 mb-6">{status}</p>
        <div className="bg-gray-800 rounded-lg p-6 mb-4">
          <div className="flex justify-between mb-2">
            <span className="text-gray-400">Players</span>
            <span>{game.players}</span>
          </div>
          {game.islands > 0 && (
            <div className="flex justify-between mb-2">
              <span className="text-gray-400">Islands</span>
              <span>{game.islands}</span>
            </div>
          )}
          <div className="flex justify-between mb-2">
            <span className="text-gray-400">Created</span>
            <span>{game.created ? new Date(game.created).toLocaleDateString() : '-'}</span>
          </div>
          {game.started && (
            <div className="flex justify-between">
              <span className="text-gray-400">Starts</span>
              <span>{new Date(game.started).toLocaleString()}</span>
            </div>
          )}
        </div>
        {!game.mapped && (
          <button
            onClick={handleAddBot}
            disabled={addingBot}
            className="mb-4 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed rounded text-white font-medium transition-colors"
          >
            {addingBot ? 'Adding Bot...' : 'Add Bot'}
          </button>
        )}
        <p className="text-sm text-gray-500">This page will update automatically.</p>
      </div>
    </div>
  )
}

export default function GamePage() {
  const { gameId } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const { state, initGame, refreshState } = useGame()
  const gameIdNum = gameId ? parseInt(gameId, 10) : null
  const [lobbyGame, setLobbyGame] = useState<SIGame | null>(null)

  useEffect(() => {
    if (!gameIdNum) {
      navigate('/games')
      return
    }
    getJoinedGames().then(games => {
      const game = games.find(g => g.id === gameIdNum)
      if (!game) {
        navigate('/games')
        return
      }
      if (!game.mapped) {
        setLobbyGame(game)
        return
      }
      initGame(gameIdNum, game.size)
    }).catch(() => {
      navigate('/login')
    })
  }, [gameIdNum, navigate, initGame])

  // Poll for game updates while in lobby
  useEffect(() => {
    if (!lobbyGame || !gameIdNum) return
    const interval = setInterval(() => {
      getJoinedGames().then(games => {
        const updated = games.find(g => g.id === gameIdNum)
        if (updated) {
          setLobbyGame(updated)
          if (updated.mapped) {
            clearInterval(interval)
            setLobbyGame(null)
            initGame(gameIdNum, updated.size)
          }
        }
      }).catch(() => {})
    }, 5000)
    return () => clearInterval(interval)
  }, [lobbyGame, gameIdNum, initGame])

  const onSocketMessage = useCallback((_msg: GameSocketMessage) => {
    refreshState()
  }, [refreshState])

  useGameSocket(gameIdNum, onSocketMessage)

  if (lobbyGame) {
    return <GameLobby game={lobbyGame} />
  }

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
