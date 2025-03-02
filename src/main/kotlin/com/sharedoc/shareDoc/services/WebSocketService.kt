package com.sharedoc.shareDoc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList


@Component
class WebSocketService : BinaryWebSocketHandler() {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val objectMapper = ObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        session.textMessageSizeLimit = 512 * 1024 // 512 KB
        session.binaryMessageSizeLimit = 2 * 1024 * 1024 // 2 MB
        println("New connection established: " + session.id)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

        val receivedText = message.payload
        val messageData: Map<String, String> = objectMapper.readValue(receivedText)

        when (messageData["action"]) {
            "send_message" -> broadcastMessage("📢 ${messageData["data"] ?: "No message"}")
            "ping" -> session.sendMessage(TextMessage("✅ Pong!"))
            "disconnect" -> {
                session.sendMessage(TextMessage("❌ Disconnecting..."))
                session.close()
            }
            else -> session.sendMessage(TextMessage("⚠️ Unknown action: ${messageData["action"]}"))
        }

        // Broadcast message to all connected clients
//        broadcastMessage("🔁 Broadcast: $receivedText")
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        super.handleBinaryMessage(session, message)
        // Forward the binary message to all connected clients except sender
        for (client in sessions) {
            if (client.isOpen && client.id != session.id) {
                client.sendMessage(message)
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }

    fun broadcastMessage(message: String) {
        for (session in sessions) {
            if (session.isOpen) {
                session.sendMessage(TextMessage(message))
            }
        }
    }
}