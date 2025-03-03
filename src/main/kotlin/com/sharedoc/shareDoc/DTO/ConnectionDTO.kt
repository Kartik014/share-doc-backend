package com.sharedoc.shareDoc.DTO

import java.util.Date

data class ConnectionDTO(
    val senderID: String = "",
    val receiverID: String = "",
    val connectionID: String = "",
    val status: String = "",
    val requestTime: Date = Date()
)
