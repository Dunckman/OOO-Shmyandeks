package org.shmyandeks.staff.api.controller

import org.shmyandeks.staff.api.generated.AuthApi
import org.shmyandeks.staff.api.model.LoginRequest
import org.shmyandeks.staff.security.CurrentUser
import org.shmyandeks.staff.service.AuthService
import org.shmyandeks.staff.service.CsrfService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val service: AuthService,
    private val csrfService: CsrfService,
    private val currentUser: CurrentUser,
) : AuthApi {
    override fun login(loginRequest: LoginRequest) = ResponseEntity.ok(service.login(loginRequest))
    override fun getCurrentUser() = ResponseEntity.ok(service.currentUser(currentUser.require()))
    override fun getCsrfToken() = ResponseEntity.ok(csrfService.currentToken())
    override fun logout(): ResponseEntity<Unit> {
        service.logout(currentUser.require())
        return ResponseEntity.noContent().build()
    }
}
