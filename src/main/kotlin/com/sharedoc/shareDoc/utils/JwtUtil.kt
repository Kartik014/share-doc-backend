package com.sharedoc.shareDoc.utils

import com.sharedoc.shareDoc.model.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(username: String, id: String, email: String): String {

        val claims = mapOf(
            "email" to email,
            "id" to id,
            "username" to username
        )
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(id)
            .setIssuedAt(Date())
            .signWith(secretKey)
            .compact()
    }

    fun extractId(token: String): String {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body.subject
    }

    fun validateToken(token: String, id: String): Boolean {
        return extractId(token) == id
    }

    fun extractEmail(token: String): Any? {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["email"]
    }

    fun extractIdFromClaim(token: String): Any? {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["id"]
    }

    fun extractUsername(token: String): Any? {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["username"]
    }

    fun getCurrentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if(authentication != null && authentication.principal is User){
            return (authentication.principal as User).id
        } else {
            throw IllegalArgumentException("User not authenticated")
        }
    }
}