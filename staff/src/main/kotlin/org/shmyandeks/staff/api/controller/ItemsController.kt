package org.shmyandeks.staff.api.controller

import org.shmyandeks.staff.api.generated.ItemsApi
import org.shmyandeks.staff.api.model.CreateItemRequest
import org.shmyandeks.staff.api.model.UpdateItemRequest
import org.shmyandeks.staff.security.CurrentUser
import org.shmyandeks.staff.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ItemsController(private val service: ItemService, private val currentUser: CurrentUser) : ItemsApi {
    override fun listItems(page: Int, size: Int) = ResponseEntity.ok(service.list(currentUser.require(), page, size))
    override fun getItem(itemId: UUID) = ResponseEntity.ok(service.get(currentUser.require(), itemId))
    override fun createItem(createItemRequest: CreateItemRequest) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(currentUser.require(), createItemRequest))
    override fun updateItem(itemId: UUID, updateItemRequest: UpdateItemRequest) =
        ResponseEntity.ok(service.update(currentUser.require(), itemId, updateItemRequest))
}
