package com.sharedoc.shareDoc.configuration

import com.sharedoc.shareDoc.interfaces.AuthServiceInterface
import com.sharedoc.shareDoc.utils.CustomAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig(private val authService: AuthServiceInterface) {

    @Bean
    fun authenticationManager(passwordEncoder: PasswordEncoder): AuthenticationManager {
        return ProviderManager(
            CustomAuthenticationProvider(authService, passwordEncoder)
        )
    }
}