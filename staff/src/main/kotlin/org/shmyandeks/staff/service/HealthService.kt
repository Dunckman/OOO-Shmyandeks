package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.HealthResponse

interface HealthService {
    fun health(): HealthResponse
}
