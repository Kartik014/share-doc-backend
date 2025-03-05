package com.sharedoc.shareDoc.factory

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserFactory(private val passwordEncoder: PasswordEncoder){
    fun createUser(userDTO: UserDTO): User {
        return User(
            id = userDTO.id ?: UUID.randomUUID().toString(),
            username = userDTO.username,
            email = userDTO.email,
            password = passwordEncoder.encode(userDTO.password)
        )
    }
}