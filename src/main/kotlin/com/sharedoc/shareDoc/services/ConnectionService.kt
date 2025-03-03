package com.sharedoc.shareDoc.services

import com.sharedoc.shareDoc.DTO.ConnectionDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.Connection
import com.sharedoc.shareDoc.repository.ConnectionRepo
import com.sharedoc.shareDoc.utils.JwtUtil
import com.sharedoc.shareDoc.utils.enums.ConnectionStatus
import org.springframework.stereotype.Service

@Service
class ConnectionService(private val connectionRepo: ConnectionRepo, private val jwtUtil: JwtUtil) {

    fun getConnections(): ApiResponse<List<Connection>> {
        val id = jwtUtil.getCurrentUserId()
        val connections: List<Connection> = connectionRepo.findAllByStatusAndReceiveridOrSenderid(status = ConnectionStatus.CONNECTED.name, receiverid = id, senderid = id)
        return ApiResponse(
            status = "success",
            message = "Connections fetched successfully",
            data = connections
        )
    }

    fun sendRequest(connectionDTO: ConnectionDTO): ApiResponse<Connection> {
        val (senderid, receiverid, connectionid, status, requestTime) = connectionDTO
        val id = jwtUtil.getCurrentUserId()
        val connectionid1 = "${receiverid}_${id}"
        val connectionid2 = "${id}_${receiverid}"

        val connectionIdList: List<String> = listOf(connectionid1, connectionid2)

        val existingConnection = connectionRepo.findByConnectionidInAndStatus(connectionIdList, ConnectionStatus.PENDING.name)

        return if(existingConnection != null){
            ApiResponse(
                status = "error",
                message = "Connection request already exist",
                data = existingConnection
            )
        } else {
            val newConnectionRequest = Connection(
                connectionid = "${receiverid}_${id}",
                senderid = id,
                receiverid = receiverid,
                status = ConnectionStatus.PENDING.name
            )
            connectionRepo.save(newConnectionRequest)
            ApiResponse(
                status = "success",
                message = "Connection request sent successfully",
                data = newConnectionRequest
            )
        }
    }

    fun getPendingRequests(): ApiResponse<List<Connection>>{

        val id = jwtUtil.getCurrentUserId()

        return try {
            val receivedRequestList = connectionRepo.findAllByStatusAndReceiverid(ConnectionStatus.PENDING.name, id)
            ApiResponse(
                status = "success",
                message = "Pending requests fetched successfully",
                data = receivedRequestList
            )
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}