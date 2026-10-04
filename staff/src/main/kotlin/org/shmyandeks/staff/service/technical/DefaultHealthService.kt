package org.shmyandeks.staff.service.technical

import org.shmyandeks.staff.api.model.HealthResponse
import org.shmyandeks.staff.service.HealthService
import org.springframework.stereotype.Service

@Service
class DefaultHealthService : HealthService {
    override fun health() = HealthResponse(HealthResponse.Status.UP)
}
