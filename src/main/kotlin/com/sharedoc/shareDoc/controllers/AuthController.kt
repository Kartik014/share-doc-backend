package com.sharedoc.shareDoc.controllers

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class AuthController(private val userService: UserService) {

    @PostMapping("/signUp")
    fun signUp(@RequestBody userDTO: UserDTO): ResponseEntity<ApiResponse<User>>{

        return try {
            val newUser = userService.signUp(userDTO)
            ResponseEntity(newUser, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid Request")
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}