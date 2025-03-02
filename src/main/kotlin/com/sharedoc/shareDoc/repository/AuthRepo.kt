package com.sharedoc.shareDoc.repository

import com.sharedoc.shareDoc.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthRepo: JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}