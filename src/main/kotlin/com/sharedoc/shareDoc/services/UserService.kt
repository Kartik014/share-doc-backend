package com.sharedoc.shareDoc.services

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.repository.AuthRepo
import org.springframework.stereotype.Service

@Service
class UserService(private val authRepo: AuthRepo) {

    fun addUser(userDTO: UserDTO): String {
        var name = userDTO.username
        val existingUser = authRepo.save(User(username = name))
        println(existingUser)
        return existingUser.username
    }
}