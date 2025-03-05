package com.sharedoc.shareDoc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sharedoc.shareDoc.utils.ExtensionFunctions.string
import com.sharedoc.shareDoc.utils.WebSocketServiceUtil.getMessage
import com.sharedoc.shareDoc.utils.WebSocketServiceUtil.getRecipientId
import com.sharedoc.shareDoc.utils.WebSocketServiceUtil.getSenderId
import com.sharedoc.shareDoc.utils.WebSocketServiceUtil.getSession
import com.sharedoc.shareDoc.utils.WebSocketServiceUtil.isUserConnected
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
    private val pendingRequests = ConcurrentHashMap<String, MutableSet<String>>()
    private val approvedConnections = ConcurrentHashMap<String, MutableSet<String>>()
    private val objectMapper = ObjectMapper()

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
            SEND_REQUEST.string() -> sendFileRequest(session, messageData)
            ACCEPT_REQUEST.string() -> acceptFileRequest(session, messageData)
            REJECT_REQUEST.string() -> rejectFileRequest(session, messageData)
            SEND_MESSAGE.string() -> sendMessageToUser(session,messageData)
            SET_RECIPIENT.string() -> setRecipientId(session, messageData)
            PING.string() -> session.sendMessage(TextMessage("Pong"))
            DISCONNECT.string() -> closeSession(session)
            else -> session.sendMessage(TextMessage("Unknown action: ${messageData[ACTION.string()]}"))
        }

    }

    private fun sendFileRequest(session: WebSocketSession, messageData: Map<String, String>) {
        val senderId = getSenderId(session)
        val recipientId = getRecipientId(messageData)

        if (!isUserConnected(messageData,sessions)) {
            session.sendMessage(TextMessage("User is offline or not registered"))
            return
        }

        pendingRequests.computeIfAbsent(recipientId) { mutableSetOf() }.add(senderId)
        getSession(messageData,sessions)?.sendMessage(
            TextMessage(objectMapper.writeValueAsString(
                mapOf(ACTION.string() to "receive_request", "from" to senderId)
            ))
        )
        session.sendMessage(TextMessage("Request sent to $recipientId"))
    }

    private fun acceptFileRequest(session: WebSocketSession, messageData: Map<String, String>) {
        val recipientId = getSenderId(session)
        val senderId = messageData[FROM.string()]

        val senderRequests = pendingRequests[recipientId]
        if (senderId == null || !isUserConnected(senderId,sessions)) {
            session.sendMessage(TextMessage("Request no longer valid or user is offline"))
            return
        }

        approvedConnections.computeIfAbsent(recipientId) { mutableSetOf() }.add(senderId)
        senderRequests!!.remove(senderId)
        if (senderRequests.isEmpty()) {
            pendingRequests.remove(recipientId)
        }

        sessions[senderId]?.sendMessage(
            TextMessage(objectMapper.writeValueAsString(
                mapOf(ACTION.string() to "request_accepted", "recipient" to recipientId)
            ))
        )

        session.sendMessage(TextMessage("You accepted the request from $senderId"))
        sessions[senderId]?.sendMessage(TextMessage("Your request is accepted"))
    }

    private fun rejectFileRequest(session: WebSocketSession, messageData: Map<String, String>) {
        val recipientId = getSenderId(session)
        val senderId = messageData[FROM.string()]

        val senderRequests = pendingRequests[recipientId]
        if (senderId != null) {
            senderRequests!!.remove(senderId)
            if (senderRequests.isEmpty()) {
                pendingRequests.remove(recipientId)
            }
            sessions[senderId]?.sendMessage(
                TextMessage(objectMapper.writeValueAsString(
                    mapOf(ACTION.string() to "request_rejected", "recipient" to recipientId)
                ))
            )
            session.sendMessage(TextMessage("You rejected the request from $senderId"))
            sessions[senderId]?.sendMessage(TextMessage("Your request is rejected by user"))
        }
    }

    private fun setRecipientId(session: WebSocketSession, messageData: Map<String, String>) {
        val id = getRecipientId(messageData)
        if(isUserConnected(messageData,sessions)){
            session.attributes[RECIPIENT_ID.string()] = id
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(
                mapOf(ACTION.string() to "send_image")
            )))
            getSession(messageData,sessions)?.sendMessage(TextMessage(objectMapper.writeValueAsString(
                mapOf(ACTION.string() to "receive_image","fileSize" to messageData["fileSize"])
            )))
        }
        else{
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(
                mapOf(ACTION.string() to "fail")
            )))
        }
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        super.handleBinaryMessage(session, message)
        val recipientId = session.attributes[RECIPIENT_ID.string()] as? String ?: ""
        if (recipientId != "" && isUserConnected(recipientId,sessions) && sessions[recipientId] != null) {
            sessions[recipientId]?.let {
                if (it.isOpen) {
                    it.sendMessage(message)
                }
            }
        } else {
            session.sendMessage(TextMessage("No recipient found for binary message."))
        }
    }

    fun sendMessageToUser(session: WebSocketSession,messageData: Map<String, String>) {
        if(isUserConnected(messageData,sessions)){
            getSession(messageData,sessions)?.sendMessage(TextMessage(getMessage(messageData)))
        }
        else{
            session.sendMessage(TextMessage("User is offline"))
        }
    }

    fun closeSession(session: WebSocketSession) {
        session.sendMessage(TextMessage("Disconnecting..."))
        session.close()
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = sessions.entries.find { it.value == session }?.key
        userId?.let {
            approvedConnections.remove(it)
            pendingRequests.remove(it)
            sessions.remove(it)
        }
    }

}