package com.sharedoc.shareDoc.services

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.repository.AuthRepo
import com.sharedoc.shareDoc.utils.JwtUtil
import org.springframework.beans.factory.ObjectProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(private val authRepo: AuthRepo, private val passwordEncoder: PasswordEncoder, private val jwtUtil: JwtUtil, private val authenticationManagerProvider: ObjectProvider<AuthenticationManager>) {

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

    fun logIn(userDTO: UserDTO): ApiResponse<String> {
        val (id, username, email, password) = userDTO

        val authenticationManager = authenticationManagerProvider.getIfAvailable() ?: throw IllegalStateException("AuthenticationManager not available")

        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val user = authRepo.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        val token = jwtUtil.generateToken(user.username, user.id, user.email)
        return ApiResponse(
            status = "success",
            message = "LogIn Successful",
            data = token
        )
    }

    fun findByEmail(email: String): User? {
        return authRepo.findByEmail(email)
    }
}