package org.shmyandeks.staff.security

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CurrentUser {
    fun require(): SessionUser {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw AuthenticationCredentialsNotFoundException("Требуется вход")
        }
        return authentication.principal as? SessionUser
            ?: throw AuthenticationCredentialsNotFoundException("Требуется вход")
    }
}
