package org.shmyandeks.staff.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.shmyandeks.staff.api.error.ApiErrors
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class ApiSecurityErrors(private val mapper: ObjectMapper) : AuthenticationEntryPoint, AccessDeniedHandler {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, ex: AuthenticationException) {
        write(response, 401)
    }

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, ex: AccessDeniedException) {
        val authentication = SecurityContextHolder.getContext().authentication
        val anonymous = authentication == null || !authentication.isAuthenticated || authentication is AnonymousAuthenticationToken
        val login = request.method == "POST" && request.servletPath == "/api/v1/auth/login"
        write(response, if (anonymous && !login) 401 else 403)
    }

    private fun write(response: HttpServletResponse, status: Int) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        mapper.writeValue(response.writer, ApiErrors.body(status))
    }
}
