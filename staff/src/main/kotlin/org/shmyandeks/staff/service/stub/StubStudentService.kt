package org.shmyandeks.staff.service.stub

import org.shmyandeks.staff.api.model.StudentPage
import org.shmyandeks.staff.security.SessionUser
import org.shmyandeks.staff.service.FeatureNotImplementedException
import org.shmyandeks.staff.service.StudentService
import org.springframework.stereotype.Service

@Service
class StubStudentService : StudentService {
    override fun list(user: SessionUser, page: Int, size: Int): StudentPage = throw FeatureNotImplementedException()
}
