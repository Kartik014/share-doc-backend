package com.sharedoc.shareDoc.controllers

import com.sharedoc.shareDoc.DTO.ConnectionDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.Connection
import com.sharedoc.shareDoc.services.ConnectionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/connection")
class ConnectionController(private val connectionService: ConnectionService) {

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
}