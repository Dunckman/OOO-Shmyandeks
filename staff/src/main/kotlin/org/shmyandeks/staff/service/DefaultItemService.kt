package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.CreateItemRequest
import org.shmyandeks.staff.api.model.ItemPage
import org.shmyandeks.staff.api.model.ItemResponse
import org.shmyandeks.staff.api.model.UpdateItemRequest
import org.shmyandeks.staff.repository.ItemRepository
import org.shmyandeks.staff.security.SessionUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DefaultItemService(private val repository: ItemRepository) : ItemService {
    @Transactional(readOnly = true)
    override fun list(user: SessionUser, page: Int, size: Int): ItemPage {
        val content = repository.findCatalog(size, page.toLong() * size).map { item ->
            ItemResponse(
                id = item.id,
                inventoryNumber = item.inventoryNumber,
                name = item.name,
                conditionDescription = item.conditionDescription,
                issueAllowed = item.issueAllowed,
                occupied = item.occupied,
                available = item.issueAllowed && !item.occupied,
            )
        }
        return ItemPage(content = content, page = page, propertySize = size, totalElements = repository.count())
    }

    override fun get(user: SessionUser, itemId: UUID): ItemResponse = throw FeatureNotImplementedException()
    override fun create(user: SessionUser, request: CreateItemRequest): ItemResponse = throw FeatureNotImplementedException()
    override fun update(user: SessionUser, itemId: UUID, request: UpdateItemRequest): ItemResponse = throw FeatureNotImplementedException()
}
