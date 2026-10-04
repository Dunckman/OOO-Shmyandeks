package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.CsrfTokenResponse

interface CsrfService {
    fun currentToken(): CsrfTokenResponse
}
