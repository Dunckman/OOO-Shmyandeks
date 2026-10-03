package org.shmyandeks.staff

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.postgresql.util.PSQLException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.FileSystemResource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import org.shmyandeks.staff.domain.AppUser
import org.shmyandeks.staff.domain.Item
import org.shmyandeks.staff.domain.Loan
import org.shmyandeks.staff.domain.UserRole
import java.time.Instant
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class StaffApplicationTests @Autowired constructor(
    private val jdbc: JdbcTemplate,
    private val entityManager: EntityManager,
    transactionManager: PlatformTransactionManager,
) {
    private val transaction = TransactionTemplate(transactionManager)

    @BeforeEach
    fun clearBusinessData() {
        jdbc.execute("TRUNCATE TABLE loan, item, app_user")
    }

    @Test
    fun `Liquibase applies both migrations and Hibernate validates the schema`() {
        assertEquals(2, jdbc.queryForObject("SELECT count(*) FROM databasechangelog", Int::class.java))
        assertEquals(
            listOf("001-create-users-and-items", "002-create-loans"),
            jdbc.queryForList("SELECT id FROM databasechangelog ORDER BY orderexecuted", String::class.java),
        )
    }

    @Test
    fun `all entities and return fields can be persisted and read through JPA`() {
        transaction.executeWithoutResult {
            val student = AppUser("student", "test-fixture-hash", UserRole.USER)
            val employee = AppUser("employee", "test-fixture-hash", UserRole.STAFF)
            val item = Item("JPA-001", "Ноутбук", "Исправен", true)
            entityManager.persist(student)
            entityManager.persist(employee)
            entityManager.persist(item)

            val issuedAt = Instant.parse("2026-10-04T09:00:00Z")
            val loan = Loan(item, student, employee, issuedAt, issuedAt.plusSeconds(3600), "Без повреждений")
            entityManager.persist(loan)
            entityManager.flush()
            val loanId = loan.id
            entityManager.clear()

            val stored = entityManager.find(Loan::class.java, loanId)
            assertEquals("JPA-001", stored.item.inventoryNumber)
            assertEquals(UserRole.USER, stored.borrower.role)
            assertEquals(UserRole.STAFF, stored.issuedBy.role)
            assertEquals(issuedAt, stored.issuedAt)
            assertNull(stored.returnedAt)

            stored.item.conditionDescription = "Нужна проверка"
            stored.item.issueAllowed = false
            stored.returnedAt = issuedAt.plusSeconds(7200)
            stored.returnedBy = stored.issuedBy
            stored.returnedCondition = "Разряжен аккумулятор"
            entityManager.flush()
            entityManager.clear()

            val returned = entityManager.find(Loan::class.java, loanId)
            assertEquals("Без повреждений", returned.issuedCondition)
            assertEquals(issuedAt.plusSeconds(7200), returned.returnedAt)
            assertEquals("employee", returned.returnedBy?.login)
            assertEquals("Разряжен аккумулятор", returned.returnedCondition)
            assertFalse(returned.item.issueAllowed)
        }
    }

    @Test
    fun `only one active loan is allowed but completed history does not block a new loan`() {
        val fixture = createFixture()
        val first = insertLoan(fixture)
        assertConstraint("uq_active_loan_per_item") { insertLoan(fixture) }

        completeLoan(first, fixture.employeeId)
        insertLoan(fixture)

        assertEquals(2, jdbc.queryForObject("SELECT count(*) FROM loan", Int::class.java))
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM loan WHERE returned_at IS NULL", Int::class.java))
    }

    @Test
    fun `return fields must be filled together and return cannot precede issue`() {
        val fixture = createFixture()
        val loanId = insertLoan(fixture)
        val invalidAssignments = listOf(
            "returned_at = '2026-10-04T10:00:00Z'",
            "returned_by_id = '${fixture.employeeId}'",
            "returned_condition = 'Исправен'",
            "returned_at = '2026-10-04T10:00:00Z', returned_by_id = '${fixture.employeeId}'",
            "returned_at = '2026-10-04T10:00:00Z', returned_condition = 'Исправен'",
            "returned_by_id = '${fixture.employeeId}', returned_condition = 'Исправен'",
        )
        invalidAssignments.forEach { assignment ->
            assertConstraint("ck_loan_return_fields") {
                jdbc.update("UPDATE loan SET $assignment WHERE id = ?", loanId)
            }
        }
        assertConstraint("ck_loan_returned_at") {
            completeLoan(loanId, fixture.employeeId, "2026-10-03T09:00:00Z")
        }
        assertNull(jdbc.queryForMap("SELECT returned_at FROM loan WHERE id = ?", loanId)["returned_at"])
    }

    @Test
    fun `deadline must be after issue and referenced users and items must exist`() {
        val fixture = createFixture()
        assertConstraint("ck_loan_due_at") { insertLoan(fixture, dueAt = "2026-10-04T09:00:00Z") }
        assertConstraint("ck_loan_due_at") { insertLoan(fixture, dueAt = "2026-10-03T09:00:00Z") }
        assertConstraint("fk_loan_borrower") { insertLoan(fixture.copy(studentId = UUID.randomUUID())) }
        assertConstraint("fk_loan_item") { insertLoan(fixture.copy(itemId = UUID.randomUUID())) }
    }

    @Test
    fun `deleting users or items cannot remove completed loan history`() {
        val fixture = createFixture()
        val loanId = insertLoan(fixture)
        completeLoan(loanId, fixture.employeeId)

        assertConstraint("fk_loan_item") { jdbc.update("DELETE FROM item WHERE id = ?", fixture.itemId) }
        assertConstraint("fk_loan_borrower") { jdbc.update("DELETE FROM app_user WHERE id = ?", fixture.studentId) }
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM loan", Int::class.java))
    }

    @Test
    fun `logins and inventory numbers are unique and roles are limited`() {
        val fixture = createFixture()
        assertConstraint("uq_app_user_login") {
            jdbc.update("INSERT INTO app_user (login, password_hash, role) VALUES ('student', 'test-fixture-hash', 'USER')")
        }
        assertConstraint("ck_app_user_role") {
            jdbc.update("INSERT INTO app_user (login, password_hash, role) VALUES ('unknown', 'test-fixture-hash', 'ADMIN')")
        }
        assertConstraint("ck_app_user_password_hash") {
            jdbc.update("INSERT INTO app_user (login, password_hash, role) VALUES ('empty', ' ', 'USER')")
        }
        assertConstraint("uq_item_inventory_number") {
            jdbc.update("INSERT INTO item (inventory_number, name, condition_description) VALUES ('TEST-001', 'Другой', 'Исправен')")
        }
        assertEquals(false, jdbc.queryForObject("SELECT issue_allowed FROM item WHERE id = ?", Boolean::class.java, fixture.itemId))
    }

    @Test
    fun `demo script creates three items without accounts or loans and preserves existing changes`() {
        runDemoScript()
        val originalIds = jdbc.queryForList("SELECT id FROM item ORDER BY inventory_number", UUID::class.java)
        assertEquals(3, originalIds.size)
        assertEquals(2, jdbc.queryForObject("SELECT count(*) FROM item WHERE issue_allowed", Int::class.java))
        assertEquals(0, jdbc.queryForObject("SELECT count(*) FROM app_user", Int::class.java))
        assertEquals(0, jdbc.queryForObject("SELECT count(*) FROM loan", Int::class.java))

        jdbc.update("UPDATE item SET name = 'Новое название', issue_allowed = FALSE WHERE inventory_number = 'STF-001'")
        runDemoScript()

        assertEquals(originalIds, jdbc.queryForList("SELECT id FROM item ORDER BY inventory_number", UUID::class.java))
        assertEquals("Новое название", jdbc.queryForObject("SELECT name FROM item WHERE inventory_number = 'STF-001'", String::class.java))
        assertFalse(jdbc.queryForObject("SELECT issue_allowed FROM item WHERE inventory_number = 'STF-001'", Boolean::class.java)!!)
    }

    private fun runDemoScript() {
        jdbc.dataSource!!.connection.use { connection ->
            ScriptUtils.executeSqlScript(connection, FileSystemResource("scripts/demo-data.sql"))
        }
    }

    private fun createFixture(): Fixture {
        val studentId = jdbc.queryForObject(
            "INSERT INTO app_user (login, password_hash, role) VALUES ('student', 'test-fixture-hash', 'USER') RETURNING id",
            UUID::class.java,
        )!!
        val employeeId = jdbc.queryForObject(
            "INSERT INTO app_user (login, password_hash, role) VALUES ('employee', 'test-fixture-hash', 'STAFF') RETURNING id",
            UUID::class.java,
        )!!
        val itemId = jdbc.queryForObject(
            "INSERT INTO item (inventory_number, name, condition_description) VALUES ('TEST-001', 'Предмет', 'Исправен') RETURNING id",
            UUID::class.java,
        )!!
        return Fixture(studentId, employeeId, itemId)
    }

    private fun insertLoan(fixture: Fixture, dueAt: String = "2026-10-05T09:00:00Z"): UUID =
        jdbc.queryForObject(
            """
            INSERT INTO loan (item_id, borrower_id, issued_by_id, issued_at, due_at, issued_condition)
            VALUES (?, ?, ?, '2026-10-04T09:00:00Z', ?::timestamptz, 'Исправен') RETURNING id
            """.trimIndent(),
            UUID::class.java, fixture.itemId, fixture.studentId, fixture.employeeId, dueAt,
        )!!

    private fun completeLoan(loanId: UUID, employeeId: UUID, returnedAt: String = "2026-10-04T10:00:00Z") {
        jdbc.update(
            "UPDATE loan SET returned_at = ?::timestamptz, returned_by_id = ?, returned_condition = 'Исправен' WHERE id = ?",
            returnedAt, employeeId, loanId,
        )
    }

    private fun assertConstraint(name: String, action: () -> Unit) {
        val exception = assertThrows<DataIntegrityViolationException>(action)
        assertEquals(name, (exception.mostSpecificCause as PSQLException).serverErrorMessage?.constraint)
    }

    private data class Fixture(val studentId: UUID, val employeeId: UUID, val itemId: UUID)

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer(
            "postgres:latest",
        )

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
