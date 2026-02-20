import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getToken } from '../api/auth'

export function useGameSocket(gameId: number | null, onMessage: (body: unknown) => void) {
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    if (gameId === null) return

    const token = getToken()
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      onConnect: () => {
        client.subscribe(`/topic/game/${gameId}`, message => {
          onMessage(JSON.parse(message.body))
        })
      },
      onStompError: frame => {
        console.error('STOMP error:', frame.headers['message'])
      },
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [gameId, onMessage])

  return clientRef
}
