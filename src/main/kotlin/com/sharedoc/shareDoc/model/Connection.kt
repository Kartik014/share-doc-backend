package com.sharedoc.shareDoc.model

import com.sharedoc.shareDoc.utils.enums.ConnectionStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.util.Date

@Entity
@Table(name = "connection")
data class Connection (

    @Id
    @Column(nullable = false, unique = true)
    val connectionid: String = "",

    @Column(nullable = false, unique = false)
    val senderid: String = "",

    @Column(nullable = false, unique = false)
    val receiverid: String = "",

    @Column(nullable = false, unique = false)
    var status: String = ConnectionStatus.PENDING.name,

    @CreationTimestamp
    @Column(nullable = false, unique = false)
    var requesttime: Date = Date()
)