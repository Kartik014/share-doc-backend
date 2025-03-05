package com.sharedoc.shareDoc.interfaces

import com.sharedoc.shareDoc.DTO.ConnectionDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.Connection

interface ConnectionServiceInterface {

    fun getConnections(): ApiResponse<List<Connection>>

    fun sendRequest(connectionDTO: ConnectionDTO): ApiResponse<Connection>

    fun getPendingRequests(): ApiResponse<List<Connection>>

    fun acceptIncomingRequest(connectionDTO: ConnectionDTO): ApiResponse<Connection>

    fun rejectIncomingRequest(connectionDTO: ConnectionDTO): ApiResponse<Connection>

    fun cancelSentConnectionRequest(connectionDTO: ConnectionDTO): ApiResponse<Connection>
}