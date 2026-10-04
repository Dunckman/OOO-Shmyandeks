package org.shmyandeks.staff.service.stub

import org.shmyandeks.staff.api.model.LoginRequest
import org.shmyandeks.staff.api.model.UserResponse
import org.shmyandeks.staff.security.SessionUser
import org.shmyandeks.staff.service.AuthService
import org.shmyandeks.staff.service.FeatureNotImplementedException
import org.springframework.stereotype.Service

@Service
class StubAuthService : AuthService {
    override fun login(request: LoginRequest): UserResponse = throw FeatureNotImplementedException()
    override fun logout(user: SessionUser): Unit = throw FeatureNotImplementedException()
    override fun currentUser(user: SessionUser): UserResponse = throw FeatureNotImplementedException()
}
