package org.shmyandeks.staff.api.controller

import org.shmyandeks.staff.api.generated.HealthApi
import org.shmyandeks.staff.service.HealthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(private val service: HealthService) : HealthApi {
    override fun getHealth() = ResponseEntity.ok(service.health())
}
