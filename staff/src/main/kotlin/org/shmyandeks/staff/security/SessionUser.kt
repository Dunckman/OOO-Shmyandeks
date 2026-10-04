package org.shmyandeks.staff.security

import org.shmyandeks.staff.domain.UserRole
import java.io.Serializable
import java.security.Principal
import java.util.UUID

data class SessionUser(val id: UUID, val login: String, val role: UserRole) : Principal, Serializable {
    override fun getName(): String = login
}
