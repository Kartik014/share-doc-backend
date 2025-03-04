package com.sharedoc.shareDoc.utils

import com.sharedoc.shareDoc.utils.ExtensionFunctions.string
import com.sharedoc.shareDoc.utils.enums.WebSocketActions.DATA
import com.sharedoc.shareDoc.utils.enums.WebSocketActions.TO
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

object WebSocketServiceUtil {

    fun getMessage(messageData: Map<String, String>):String{
        return messageData[DATA.string()] ?: "No message"
    }

    fun getSession(messageData: Map<String, String>,sessions :ConcurrentHashMap<String, WebSocketSession>): WebSocketSession?{
        return sessions[getUserId(messageData)]
    }

    fun isUserConnected(messageData: Map<String, String>,sessions :ConcurrentHashMap<String, WebSocketSession>): Boolean {
        val userId = getUserId(messageData)
        return sessions.containsKey(userId) && sessions[userId]?.isOpen == true
    }

    fun isUserConnected(userId: String,sessions :ConcurrentHashMap<String, WebSocketSession>): Boolean {
        return sessions.containsKey(userId) && sessions[userId]?.isOpen == true
    }

    fun getUserId(messageData: Map<String, String>):String{
        return messageData[TO.string()]?:""
    }

    fun getSenderId(session: WebSocketSession): String{
        val uri = session.uri
        val userId = uri?.query?.split("userId=")?.getOrNull(1) ?: session.id
        return userId
    }

}