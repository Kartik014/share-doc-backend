package com.sharedoc.shareDoc.middlewares

import com.sharedoc.shareDoc.model.User
import com.sharedoc.shareDoc.utils.JwtUtil
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthFilter(private val jwtUtil: JwtUtil): OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                val token = authHeader.split(" ")[1]
                val email: String = jwtUtil.extractEmail(token).toString()
                val userID: String = jwtUtil.extractIdFromClaim(token).toString()
                val username: String = jwtUtil.extractUsername(token).toString()

                if(SecurityContextHolder.getContext().authentication == null){
                    if(jwtUtil.validateToken(token, userID)){
                        val user = User(userID, username, email, "")
                        val authToken = UsernamePasswordAuthenticationToken(user, null, emptyList())
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            }
        } catch (ex: ExpiredJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired")
            return
        } catch (ex: MalformedJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
            return
        } catch (ex: UnsupportedJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported token")
            return
        } catch (ex: IllegalArgumentException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token claims string is empty")
            return
        }

        filterChain.doFilter(request, response)
    }
}