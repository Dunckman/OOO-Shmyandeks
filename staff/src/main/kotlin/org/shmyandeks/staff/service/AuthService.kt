package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.LoginRequest
import org.shmyandeks.staff.api.model.UserResponse
import org.shmyandeks.staff.security.SessionUser

interface AuthService {
    fun login(request: LoginRequest): UserResponse
    fun logout(user: SessionUser)
    fun currentUser(user: SessionUser): UserResponse
}
