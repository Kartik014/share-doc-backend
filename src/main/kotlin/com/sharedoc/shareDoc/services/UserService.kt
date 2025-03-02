package com.sharedoc.shareDoc.services

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.repository.AuthRepo
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val authRepo: AuthRepo, private val passwordEncoder: PasswordEncoder) {

    fun signUp(userDTO: UserDTO): ApiResponse<User> {
        val (id, username, email, password) = userDTO

        if(authRepo.findByEmail(email) != null){
            throw IllegalArgumentException("Email already registered")
        }
        val hashedPassword = passwordEncoder.encode(password)
        val newUser = User(
            id = id?: UUID.randomUUID().toString(),
            username = username,
            email = email,
            password = hashedPassword
        )
        val savedUser: User = authRepo.save(newUser)
        return ApiResponse(
            status = "success",
            message = "User ${savedUser.username} created successfully",
            data = savedUser
        )
    }
}