package org.shmyandeks.staff.service.technical

import jakarta.servlet.http.HttpServletRequest
import org.shmyandeks.staff.api.model.CsrfTokenResponse
import org.shmyandeks.staff.service.CsrfService
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Service

@Service
class SessionCsrfService(private val request: HttpServletRequest) : CsrfService {
    override fun currentToken(): CsrfTokenResponse {
        val csrf = request.getAttribute(CsrfToken::class.java.name) as CsrfToken
        return CsrfTokenResponse(csrf.token, CsrfTokenResponse.HeaderName.forValue(csrf.headerName))
    }
}
