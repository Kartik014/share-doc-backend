package com.sharedoc.shareDoc.interfaces

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User

interface AuthServiceInterface {

    fun signUp(userDTO: UserDTO): ApiResponse<User>

    fun logIn(userDTO: UserDTO): ApiResponse<String>

    fun findByEmail(email: String): User?
}