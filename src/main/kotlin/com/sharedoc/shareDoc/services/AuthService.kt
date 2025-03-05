package com.sharedoc.shareDoc.services

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.factory.AuthenticatorFactory
import com.sharedoc.shareDoc.factory.UserFactory
import com.sharedoc.shareDoc.interfaces.AuthServiceInterface
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.repository.AuthRepo
import com.sharedoc.shareDoc.utils.JwtUtil
import org.springframework.stereotype.Service

@Service
class AuthService(private val authRepo: AuthRepo, private val userFactory: UserFactory, private val jwtUtil: JwtUtil, private val authenticatorFactory: AuthenticatorFactory): AuthServiceInterface {

    override fun signUp(userDTO: UserDTO): ApiResponse<User> {
        val email = userDTO.email

        if(authRepo.findByEmail(email) != null){
            throw IllegalArgumentException("Email already registered")
        }
        val newUser = userFactory.createUser(userDTO)
        val savedUser: User = authRepo.save(newUser)
        return ApiResponse(
            status = "success",
            message = "User ${savedUser.username} created successfully",
            data = savedUser
        )
    }

    override fun logIn(userDTO: UserDTO): ApiResponse<String> {
        val email = userDTO.email
        val password = userDTO.password

        authenticatorFactory.authenticate(email, password)
        val user = authRepo.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        val token = jwtUtil.generateToken(user.username, user.id, user.email)
        return ApiResponse(
            status = "success",
            message = "LogIn Successful",
            data = token
        )
    }

    override fun findByEmail(email: String): User? {
        return authRepo.findByEmail(email)
    }
}