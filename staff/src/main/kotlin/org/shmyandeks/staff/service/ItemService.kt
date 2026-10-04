package org.shmyandeks.staff.service

import org.shmyandeks.staff.api.model.CreateItemRequest
import org.shmyandeks.staff.api.model.ItemPage
import org.shmyandeks.staff.api.model.ItemResponse
import org.shmyandeks.staff.api.model.UpdateItemRequest
import org.shmyandeks.staff.security.SessionUser
import java.util.UUID

interface ItemService {
    fun list(user: SessionUser, page: Int, size: Int): ItemPage
    fun get(user: SessionUser, itemId: UUID): ItemResponse
    fun create(user: SessionUser, request: CreateItemRequest): ItemResponse
    fun update(user: SessionUser, itemId: UUID, request: UpdateItemRequest): ItemResponse
}
