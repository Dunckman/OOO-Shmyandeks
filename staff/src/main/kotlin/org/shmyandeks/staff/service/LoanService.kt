package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.CreateLoanRequest
import org.shmyandeks.staff.api.model.LoanPage
import org.shmyandeks.staff.api.model.LoanResponse
import org.shmyandeks.staff.api.model.LoanStatus
import org.shmyandeks.staff.api.model.ReturnLoanRequest
import org.shmyandeks.staff.security.SessionUser
import java.util.UUID

interface LoanService {
    fun list(user: SessionUser, page: Int, size: Int, status: LoanStatus?): LoanPage
    fun get(user: SessionUser, loanId: UUID): LoanResponse
    fun create(user: SessionUser, request: CreateLoanRequest): LoanResponse
    fun returnLoan(user: SessionUser, loanId: UUID, request: ReturnLoanRequest): LoanResponse
}
