package com.sharedoc.shareDoc.utils.enums

enum class ConnectionStatus {
    PENDING, REJECTED, CONNECTED, CANCELED;

    override fun toString(): String {
        return name.lowercase()
    }
}