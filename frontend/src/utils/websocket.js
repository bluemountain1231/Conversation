import SockJS from 'sockjs-client/dist/sockjs'
import { Client } from '@stomp/stompjs'

let stompClient = null

export function connectWebSocket(userId, onNotification) {
  if (stompClient) return

  stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
        const data = JSON.parse(message.body)
        if (onNotification) onNotification(data)
      })
    }
  })

  stompClient.activate()
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate()
    stompClient = null
  }
}
