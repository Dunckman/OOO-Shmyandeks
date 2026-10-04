package org.shmyandeks.staff.service.stub

import org.shmyandeks.staff.api.model.CreateLoanRequest
import org.shmyandeks.staff.api.model.LoanPage
import org.shmyandeks.staff.api.model.LoanResponse
import org.shmyandeks.staff.api.model.LoanStatus
import org.shmyandeks.staff.api.model.ReturnLoanRequest
import org.shmyandeks.staff.security.SessionUser
import org.shmyandeks.staff.service.FeatureNotImplementedException
import org.shmyandeks.staff.service.LoanService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StubLoanService : LoanService {
    override fun list(user: SessionUser, page: Int, size: Int, status: LoanStatus?): LoanPage = throw FeatureNotImplementedException()
    override fun get(user: SessionUser, loanId: UUID): LoanResponse = throw FeatureNotImplementedException()
    override fun create(user: SessionUser, request: CreateLoanRequest): LoanResponse = throw FeatureNotImplementedException()
    override fun returnLoan(user: SessionUser, loanId: UUID, request: ReturnLoanRequest): LoanResponse = throw FeatureNotImplementedException()
}
