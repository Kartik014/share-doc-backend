package com.sharedoc.shareDoc.DTO

data class UserDTO(
    val id: String? = null,
    val username: String = "",
    val email: String = "",
    val password: String = ""
)