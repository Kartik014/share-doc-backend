package com.sharedoc.shareDoc.controllers

import com.sharedoc.shareDoc.DTO.ConnectionDTO
import com.sharedoc.shareDoc.interfaces.ConnectionServiceInterface
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.Connection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/connection")
class ConnectionController(private val connectionService: ConnectionServiceInterface) {

    @GetMapping("/getconnects")
    fun getConnections(): ResponseEntity<ApiResponse<List<Connection>>>{

        return try {
            val connections = connectionService.getConnections()
            ResponseEntity(connections, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PostMapping("/request/userid={userid}")
    fun sendRequest(@PathVariable userid: String): ResponseEntity<ApiResponse<Connection>>{

        val connectionDTO = ConnectionDTO(
            senderID = "",
            receiverID = userid,
            connectionID = "",
            status = "",
            requestTime = Date()
        )

        return try {
            val newConnectionRequest = connectionService.sendRequest(connectionDTO)
            ResponseEntity(newConnectionRequest, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/request/received")
    fun getPendingRequests(): ResponseEntity<ApiResponse<List<Connection>>> {

        return try {
            val receivedRequests = connectionService.getPendingRequests()
            ResponseEntity(receivedRequests, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PatchMapping("/request/accept/userid={userid}")
    fun acceptIncomingRequest(@PathVariable userid: String, @RequestBody connectionDTO: ConnectionDTO): ResponseEntity<ApiResponse<Connection>> {

        val status = connectionDTO.status
        val connectionID = connectionDTO.connectionID
        val newConnectionDTO = ConnectionDTO(
            senderID = userid,
            receiverID = "",
            connectionID = connectionID,
            status = status
        )

        return try {
            val acceptedRequest = connectionService.acceptIncomingRequest(newConnectionDTO)
            ResponseEntity(acceptedRequest, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PatchMapping("/request/reject/userid={userid}")
    fun rejectIncomingRequest(@PathVariable userid: String, @RequestBody connectionDTO: ConnectionDTO): ResponseEntity<ApiResponse<Connection>> {

        val status = connectionDTO.status
        val connectionID = connectionDTO.connectionID
        val newConnectionDTO = ConnectionDTO(
            senderID = userid,
            receiverID = "",
            connectionID = connectionID,
            status = status
        )

        return try {
            val rejectedRequest = connectionService.rejectIncomingRequest(newConnectionDTO)
            ResponseEntity(rejectedRequest, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal server error")
        }
    }

    @PatchMapping("/userid={userid}/request/cancel")
    fun cancelSentConnectionRequest(@PathVariable userid: String, @RequestBody connectionDTO: ConnectionDTO): ResponseEntity<ApiResponse<Connection>> {

        val status = connectionDTO.status
        val connectionID = connectionDTO.connectionID
        val newConnectionDTO = ConnectionDTO(
            senderID = userid,
            receiverID = "",
            connectionID = connectionID,
            status = status
        )

        return try {
            val canceledRequest = connectionService.cancelSentConnectionRequest(newConnectionDTO)
            ResponseEntity(canceledRequest, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal server error")
        }
    }
}