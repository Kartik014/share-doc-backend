package com.sharedoc.shareDoc.controllers

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.interfaces.AuthServiceInterface
import com.sharedoc.shareDoc.model.ApiResponse
import com.sharedoc.shareDoc.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthServiceInterface) {

    @PostMapping("/signUp")
    fun signUp(@RequestBody userDTO: UserDTO): ResponseEntity<ApiResponse<User>>{

        return try {
            val newUser = authService.signUp(userDTO)
            ResponseEntity(newUser, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PostMapping("/logIn")
    fun logIn(@RequestBody userDTO: UserDTO): ResponseEntity<ApiResponse<String>>{

        return try {
            val loggedInUser = authService.logIn(userDTO)
            ResponseEntity(loggedInUser, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}