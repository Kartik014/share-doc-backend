package com.sharedoc.shareDoc.utils

import com.sharedoc.shareDoc.services.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder

class CustomAuthenticationProvider(private val userService: UserService, private val passwordEncoder: PasswordEncoder): AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val email = authentication!!.name
        val password = authentication.credentials.toString()

        val user = userService.findByEmail(email)
            ?: throw IllegalArgumentException("User not found")

        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        return UsernamePasswordAuthenticationToken(email, password, emptyList())
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}