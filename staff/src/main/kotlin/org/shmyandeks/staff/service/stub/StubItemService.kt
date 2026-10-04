package org.shmyandeks.staff.service.stub

import org.shmyandeks.staff.api.model.CreateItemRequest
import org.shmyandeks.staff.api.model.ItemPage
import org.shmyandeks.staff.api.model.ItemResponse
import org.shmyandeks.staff.api.model.UpdateItemRequest
import org.shmyandeks.staff.security.SessionUser
import org.shmyandeks.staff.service.FeatureNotImplementedException
import org.shmyandeks.staff.service.ItemService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StubItemService : ItemService {
    override fun list(user: SessionUser, page: Int, size: Int): ItemPage = throw FeatureNotImplementedException()
    override fun get(user: SessionUser, itemId: UUID): ItemResponse = throw FeatureNotImplementedException()
    override fun create(user: SessionUser, request: CreateItemRequest): ItemResponse = throw FeatureNotImplementedException()
    override fun update(user: SessionUser, itemId: UUID, request: UpdateItemRequest): ItemResponse = throw FeatureNotImplementedException()
}
