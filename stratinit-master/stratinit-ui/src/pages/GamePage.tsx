import { useEffect, useCallback, useState } from 'react'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
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
    <div data-testid="game-lobby" className="flex items-center justify-center h-full">
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
            data-testid="add-bot-button"
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
  const location = useLocation()
  const { state, initGame, refreshState, commandError } = useGame()
  const gameIdNum = gameId ? parseInt(gameId, 10) : null
  const [lobbyGame, setLobbyGame] = useState<SIGame | null>(null)
  const passedGame = (location.state as { game?: SIGame } | null)?.game

  useEffect(() => {
    if (!gameIdNum) {
      navigate('/games')
      return
    }
    // If navigated with a pre-loaded game (e.g. from Quick Blitz), use it directly
    if (passedGame && passedGame.id === gameIdNum && passedGame.mapped) {
      initGame(gameIdNum, passedGame.size)
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
  }, [gameIdNum, navigate, initGame, passedGame])

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
    }, 2000)
    return () => clearInterval(interval)
  }, [lobbyGame, gameIdNum, initGame])

  const onSocketMessage = useCallback((_msg: GameSocketMessage) => {
    refreshState()
  }, [refreshState])

  useGameSocket(gameIdNum, onSocketMessage)

  // Navigate to stats page when game ends
  useEffect(() => {
    if (!state.update?.gameEnds || !gameIdNum) return
    const endsTime = new Date(state.update.gameEnds).getTime()
    const remaining = endsTime - Date.now()
    if (remaining <= 0) {
      navigate(`/stats/${gameIdNum}`)
      return
    }
    // Schedule navigation for when the game ends
    const timer = setTimeout(() => {
      navigate(`/stats/${gameIdNum}`)
    }, remaining)
    return () => clearTimeout(timer)
  }, [state.update?.gameEnds, gameIdNum, navigate])

  // Polling fallback: refresh every 10s in case WebSocket is disconnected
  useEffect(() => {
    if (!state.update) return
    const interval = setInterval(() => refreshState(), 10000)
    return () => clearInterval(interval)
  }, [state.update, refreshState])

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
    <div className="flex h-full relative">
      <div className="flex-1 overflow-hidden p-2">
        <GameMap />
      </div>
      <SidePanel />
      {commandError && (
        <div className="absolute top-4 left-1/2 -translate-x-1/2 bg-red-900/90 text-red-100 px-4 py-2 rounded shadow-lg text-sm">
          {commandError}
        </div>
      )}
    </div>
  )
}
