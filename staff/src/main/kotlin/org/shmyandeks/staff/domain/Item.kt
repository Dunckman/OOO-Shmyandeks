package org.shmyandeks.staff.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "item")
class Item(
    @Column(name = "inventory_number", nullable = false, length = 100)
    var inventoryNumber: String,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(name = "condition_description", nullable = false, columnDefinition = "text")
    var conditionDescription: String,

    @Column(name = "issue_allowed", nullable = false)
    var issueAllowed: Boolean = false,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
        protected set
}

