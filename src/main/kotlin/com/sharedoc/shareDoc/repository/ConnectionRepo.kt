package com.sharedoc.shareDoc.repository

import com.sharedoc.shareDoc.model.Connection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ConnectionRepo: JpaRepository<Connection, String> {

    fun findAllByStatusAndReceiveridOrSenderid(status: String, receiverid: String, senderid: String): List<Connection>

    fun findByConnectionidInAndStatus(connectionid: List<String>, status: String): Connection?

    fun findAllByStatusAndReceiverid(status: String, receiverid: String): List<Connection>

    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value = "UPDATE connection c SET c.status = :status WHERE c.senderid = :senderid AND c.receiverid = :receiverid AND c.connectionid = :connectionid RETURNING *"
    )
    fun updateStatus(
        @Param("senderid") senderid: String,
        @Param("receiverid") receiverid: String,
        @Param("connectionid") connectionid: String,
        @Param("status") status: String
    ): Connection?
}