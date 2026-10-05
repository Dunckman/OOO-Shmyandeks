package org.shmyandeks.staff.repository

import org.shmyandeks.staff.domain.Item
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ItemRepository : Repository<Item, UUID> {
    @Query(
        value = """
            SELECT i.id, i.inventory_number AS "inventoryNumber", i.name,
                   i.condition_description AS "conditionDescription", i.issue_allowed AS "issueAllowed",
                   EXISTS (SELECT 1 FROM loan l WHERE l.item_id = i.id AND l.returned_at IS NULL) AS occupied
            FROM item i
            ORDER BY i.inventory_number, i.id
            LIMIT :limit OFFSET :offset
        """,
        nativeQuery = true,
    )
    fun findCatalog(@Param("limit") limit: Int, @Param("offset") offset: Long): List<CatalogItem>

    fun count(): Long
}

interface CatalogItem {
    val id: UUID
    val inventoryNumber: String
    val name: String
    val conditionDescription: String
    val issueAllowed: Boolean
    val occupied: Boolean
}
