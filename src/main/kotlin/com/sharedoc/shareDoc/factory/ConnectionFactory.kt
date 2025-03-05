package com.sharedoc.shareDoc.factory

import com.sharedoc.shareDoc.model.Connection
import com.sharedoc.shareDoc.repository.ConnectionRepo
import com.sharedoc.shareDoc.utils.ExtensionFunctions.string
import com.sharedoc.shareDoc.utils.enums.ConnectionStatus
import org.springframework.stereotype.Component

@Component
class ConnectionFactory(private val connectionRepo: ConnectionRepo) {
    fun checkExistingConnection(receiverid: String, id: String): Connection? {
        val connectionid1 = "${receiverid}_${id}"
        val connectionid2 = "${id}_${receiverid}"

        val connectionIdList: List<String> = listOf(connectionid1, connectionid2)

        val existingConnection = connectionRepo.findByConnectionidInAndStatus(connectionIdList, ConnectionStatus.PENDING.string())
        return existingConnection
    }

    fun createConnection(senderId: String, receiverId: String): Connection {
        return Connection(
            connectionid = "${senderId}_$receiverId",
            senderid = senderId,
            receiverid = receiverId,
            status = ConnectionStatus.PENDING.string()
        )
    }
}