package org.shmyandeks.staff.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "loan")
class Loan(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, updatable = false)
    val item: Item,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false, updatable = false)
    val borrower: AppUser,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issued_by_id", nullable = false, updatable = false)
    val issuedBy: AppUser,

    @Column(name = "issued_at", nullable = false, updatable = false)
    val issuedAt: Instant,

    @Column(name = "due_at", nullable = false, updatable = false)
    val dueAt: Instant,

    @Column(name = "issued_condition", nullable = false, updatable = false, columnDefinition = "text")
    val issuedCondition: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
        protected set

    @Column(name = "returned_at")
    var returnedAt: Instant? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by_id")
    var returnedBy: AppUser? = null

    @Column(name = "returned_condition", columnDefinition = "text")
    var returnedCondition: String? = null
}

