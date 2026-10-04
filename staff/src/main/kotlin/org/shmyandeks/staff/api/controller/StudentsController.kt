package org.shmyandeks.staff.api.controller

import org.shmyandeks.staff.api.generated.StudentsApi
import org.shmyandeks.staff.security.CurrentUser
import org.shmyandeks.staff.service.StudentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentsController(private val service: StudentService, private val currentUser: CurrentUser) : StudentsApi {
    override fun listStudents(page: Int, size: Int) = ResponseEntity.ok(service.list(currentUser.require(), page, size))
}
