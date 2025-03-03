package com.sharedoc.shareDoc.repository

import com.sharedoc.shareDoc.model.Connection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConnectionRepo: JpaRepository<Connection, String> {

    fun findAllByStatusAndReceiveridOrSenderid(status: String, receiverid: String, senderid: String): List<Connection>

    fun findByConnectionidInAndStatus(connectionid: List<String>, status: String): Connection?

    fun findAllByStatusAndReceiverid(status: String, receiverid: String): List<Connection>
}