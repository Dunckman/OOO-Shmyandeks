package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.StudentPage
import org.shmyandeks.staff.security.SessionUser

interface StudentService {
    fun list(user: SessionUser, page: Int, size: Int): StudentPage
}
