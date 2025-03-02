package com.sharedoc.shareDoc.controllers

import com.sharedoc.shareDoc.DTO.UserDTO
import com.sharedoc.shareDoc.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(private val userService: UserService) {

    @PostMapping("/addUser")
    fun addUser(@RequestBody userDTO: UserDTO): ResponseEntity<String>{

        return try {
            val newUser = userService.addUser(userDTO)
            ResponseEntity(newUser, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }
}