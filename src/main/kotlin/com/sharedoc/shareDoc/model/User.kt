package com.sharedoc.shareDoc.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(unique = true, nullable = false)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false, unique = true)
    val username: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val password: String = ""
)
