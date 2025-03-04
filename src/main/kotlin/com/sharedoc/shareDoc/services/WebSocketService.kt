package com.sharedoc.shareDoc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sharedoc.shareDoc.utils.ExtensionFunctions.string
import com.sharedoc.shareDoc.utils.enums.WebSocketActions.*
import org.springframework.stereotype.Component
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import java.util.concurrent.ConcurrentHashMap


@Component
class WebSocketService : BinaryWebSocketHandler() {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val objectMapper = ObjectMapper()
    var recipientId = ""

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri = session.uri
        val userId = uri?.query?.split("userId=")?.getOrNull(1) ?: session.id
        sessions[userId] = session
        session.textMessageSizeLimit = 512 * 1024 // 512 KB
        session.binaryMessageSizeLimit = 2 * 1024 * 1024 // 2 MB
        println("New connection established: " + session.id)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

        val receivedText = message.payload
        val messageData: Map<String, String> = objectMapper.readValue(receivedText)

        when (messageData[ACTION.string()]) {
            SEND_MESSAGE.string() -> sendMessageToUser(session,messageData)
            SET_RECIPIENT.string() -> setRecipientId(session, messageData)
            PING.string() -> session.sendMessage(TextMessage("Pong"))
            DISCONNECT.string() -> closeSession(session)
            else -> session.sendMessage(TextMessage("Unknown action: ${messageData[ACTION.string()]}"))
        }

    }

    private fun setRecipientId(session: WebSocketSession, messageData: Map<String, String>) {
        val id = getUserId(messageData)
        if(isUserConnected(messageData)){
            recipientId = id
            session.sendMessage(TextMessage("$id is set as Recipient"))
        }
        else{
            session.sendMessage(TextMessage("User is offline or not registered"))
        }
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        super.handleBinaryMessage(session, message)

        if (recipientId != "") {
            sessions[recipientId]?.let {
                if (it.isOpen) {
                    it.sendMessage(message)
                }
            }
        } else {
            session.sendMessage(TextMessage("No recipient specified for binary message."))
        }
    }

    fun sendMessageToUser(session: WebSocketSession,messageData: Map<String, String>) {
        if(isUserConnected(messageData)){
            getSession(messageData)?.sendMessage(TextMessage(getMessage(messageData)))
        }
        else{
            session.sendMessage(TextMessage("User is offline"))
        }
    }

    fun getMessage(messageData: Map<String, String>):String{
        return messageData[DATA.string()] ?: "No message"
    }

    fun getSession(messageData: Map<String, String>):WebSocketSession?{
        return sessions[getUserId(messageData)]
    }

    fun isUserConnected(messageData: Map<String, String>): Boolean {
        val userId = getUserId(messageData)
        return sessions.containsKey(userId) && sessions[userId]?.isOpen == true
    }

    fun getUserId(messageData: Map<String, String>):String{
        return messageData[TO.string()]?:""
    }

    fun closeSession(session: WebSocketSession) {
        session.sendMessage(TextMessage("Disconnecting..."))
        session.close()
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = sessions.entries.find { it.value == session }?.key
        userId?.let { sessions.remove(it) }
    }

}