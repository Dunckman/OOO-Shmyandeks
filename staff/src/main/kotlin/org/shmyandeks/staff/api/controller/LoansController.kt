package org.shmyandeks.staff.api.controller

import org.shmyandeks.staff.api.generated.LoansApi
import org.shmyandeks.staff.api.model.CreateLoanRequest
import org.shmyandeks.staff.api.model.LoanStatus
import org.shmyandeks.staff.api.model.ReturnLoanRequest
import org.shmyandeks.staff.security.CurrentUser
import org.shmyandeks.staff.service.LoanService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class LoansController(private val service: LoanService, private val currentUser: CurrentUser) : LoansApi {
    override fun listLoans(page: Int, size: Int, status: LoanStatus?) =
        ResponseEntity.ok(service.list(currentUser.require(), page, size, status))
    override fun getLoan(loanId: UUID) = ResponseEntity.ok(service.get(currentUser.require(), loanId))
    override fun createLoan(createLoanRequest: CreateLoanRequest) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(currentUser.require(), createLoanRequest))
    override fun returnLoan(loanId: UUID, returnLoanRequest: ReturnLoanRequest) =
        ResponseEntity.ok(service.returnLoan(currentUser.require(), loanId, returnLoanRequest))
}
