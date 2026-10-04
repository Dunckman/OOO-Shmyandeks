package org.shmyandeks.staff.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity, errors: ApiSecurityErrors): SecurityFilterChain {
        http
            .authorizeHttpRequests { rules ->
                rules.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .requestMatchers(HttpMethod.GET, "/health", "/api/v1/auth/csrf", "/openapi/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/students").hasRole("STAFF")
                    .requestMatchers(HttpMethod.POST, "/api/v1/items", "/api/v1/loans", "/api/v1/loans/*/return").hasRole("STAFF")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/items/*").hasRole("STAFF")
                    .requestMatchers(HttpMethod.GET, "/api/v1/auth/me", "/api/v1/items", "/api/v1/items/*", "/api/v1/loans", "/api/v1/loans/*").hasAnyRole("USER", "STAFF")
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").hasAnyRole("USER", "STAFF")
                    .anyRequest().denyAll()
            }
            .csrf { it.csrfTokenRepository(HttpSessionCsrfTokenRepository()) }
            .exceptionHandling { it.authenticationEntryPoint(errors).accessDeniedHandler(errors) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .requestCache { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager = AuthenticationManager {
        throw AuthenticationServiceException("Вход пока не реализован")
    }
}
