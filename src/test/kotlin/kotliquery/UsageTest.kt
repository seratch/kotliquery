package kotliquery

import org.junit.Before
import org.junit.Test
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.*

class UsageTest {

    data class Member(
        val id: Int,
        val name: String?,
        val createdAt: ZonedDateTime
    )

    private val toMember: (Row) -> Member = { row ->
        Member(row.int("id"), row.stringOrNull("name"), row.zonedDateTime("created_at"))
    }

    private val insert = "insert into members (name,  created_at) values (?, ?)"

    private val dropTableStmt = "drop table members if exists"

    private val createTableStmt = """
        create table members (
            id serial primary key,
            name varchar(64),
            created_at timestamp not null
        )
    """.trimIndent()

    @Before
    fun before() {
        using(sessionOf(testDataSource)) { session ->
            session.execute(queryOf(dropTableStmt))
            session.execute(queryOf(createTableStmt))
        }
    }

    @Test
    fun sessionUsage() {
        using(sessionOf(testDataSource)) { session ->

            session.update(queryOf(insert, "Alice", Date()))
            session.update(queryOf(insert, "Bob", Date()))

            val ids: List<Int> = session.list(queryOf("select id from members")) { row -> row.int("id") }
            assertEquals(2, ids.size)

            val members: List<Member> = session.list(queryOf("select id, name, created_at from members"), toMember)
            assertEquals(2, members.size)

            var count = 0
            session.forEach(queryOf("select id from members")) { row ->
                count++
                assertNotNull(row.int("id"))
            }
            assertEquals(2, count)

            val nameQuery = "select id, name, created_at from members where name = ?"
            val alice: Member? = session.single(queryOf(nameQuery, "Alice"), toMember)
            assertNotNull(alice)

            val bob: Member? = session.single(queryOf(nameQuery, "Bob"), toMember)
            assertNotNull(bob)

            val chris: Member? = session.single(queryOf(nameQuery, "Chris"), toMember)
            assertNull(chris)
        }
    }


    @Test
    fun addNewWithId() {
        using(sessionOf(testDataSource, returnGeneratedKey = true)) { session ->
            // session usage example
            val createdID = session.run(queryOf(insert, "Fred", Date()).asUpdateAndReturnGeneratedKey)
            assertEquals(1, createdID)

            //action usage example
            val createdID2 = session.updateAndReturnGeneratedKey(queryOf(insert, "Jane", Date()))
            assertEquals(2, createdID2)
        }
    }


    @Test
    fun actionUsage() {
        using(sessionOf(testDataSource)) { session ->

            session.run(queryOf(insert, "Alice", Date()).asUpdate)
            session.run(queryOf(insert, "Bob", Date()).asUpdate)

            val ids: List<Int> = session.run(queryOf("select id from members").map { row -> row.int("id") }.asList)
            assertEquals(2, ids.size)

            val members: List<Member> =
                session.run(queryOf("select id, name, created_at from members").map(toMember).asList)
            assertEquals(2, members.size)

            var count = 0
            session.forEach(queryOf("select id from members")) { row ->
                count++
                assertNotNull(row.int("id"))
            }
            assertEquals(2, count)

            val nameQuery = "select id, name, created_at from members where name = ?"
            val alice: Member? = session.run(queryOf(nameQuery, "Alice").map(toMember).asSingle)
            assertNotNull(alice)

            val bob: Member? = session.run(queryOf(nameQuery, "Bob").map(toMember).asSingle)
            assertNotNull(bob)

            val chris: Member? = session.run(queryOf(nameQuery, "Chris").map(toMember).asSingle)
            assertNull(chris)
        }
    }

    @Test
    fun transactionUsage() {
        using(sessionOf(testDataSource)) { session ->

            val idsQuery = queryOf("select id from members").map { row -> row.int("id") }.asList

            session.run(queryOf(insert, "Alice", Date()).asUpdate)
            session.transaction { tx -> tx.run(queryOf(insert, "Bob", Date()).asUpdate) }
            assertEquals(2, session.run(idsQuery).size)

            try {
                session.transaction { tx ->
                    tx.run(queryOf(insert, "Chris", Date()).asUpdate)
                    assertEquals(3, tx.run(idsQuery).size)
                    throw RuntimeException()
                }
            } catch (e: RuntimeException) {
            }
            assertEquals(2, session.run(idsQuery).size)
        }
    }

    @Test
    fun testHikariCPUsage() {

        val helloDataSource = HikariCP.init("hello","jdbc:h2:mem:hello", "user", "pass")

        assertNotSame(helloDataSource, testDataSource)
        assertSame(helloDataSource, HikariCP.dataSource("hello"))

        using(sessionOf(helloDataSource, returnGeneratedKey = true)) { session ->

            session.execute(queryOf(createTableStmt))

            listOf("Alice", "Bob").forEach { name ->
                session.update(queryOf(insert, name, Date()))
            }
            val ids: List<Int> = session.list(queryOf("select id from members")) { row -> row.int("id") }
            assertEquals(2, ids.size)
        }

        helloDataSource.close()
    }


    @Test
    fun stmtParamPopulation() {
        withPreparedStmt(
            queryOf(
                """SELECT * FROM dual t
            WHERE (:param1 IS NULL OR :param2 = :param2)
            AND (:param2 IS NULL OR :param1 = :param3)
            AND (:param3 IS NULL OR :param3 = :param1)""",
                paramMap = mapOf(
                    "param1" to "1",
                    "param2" to 2,
                    "param3" to true
                )
            )
        ) { preparedStmt ->
            assertEquals(
                """SELECT * FROM dual t
            WHERE (? IS NULL OR ? = ?) AND (? IS NULL OR ? = ?) AND (? IS NULL OR ? = ?)
            {1: '1', 2: 2, 3: 2, 4: 2, 5: '1', 6: TRUE, 7: TRUE, 8: TRUE, 9: '1'}""".normalizeSpaces(),
                preparedStmt.toString().extractQueryFromPreparedStmt()
            )
        }

        withPreparedStmt(
            queryOf(
                """SELECT * FROM dual t WHERE (:param1 IS NULL OR :param2 = :param2)""",
                paramMap = mapOf("param2" to 2)
            )
        ) { preparedStmt ->
            assertEquals(
                """SELECT * FROM dual t WHERE (? IS NULL OR ? = ?)
            {1: NULL, 2: 2, 3: 2}""".normalizeSpaces(),
                preparedStmt.toString().extractQueryFromPreparedStmt()
            )
        }

    }

    @Test
    fun nullParams() {
        withPreparedStmt(queryOf("SELECT * FROM dual t WHERE ? IS NULL", null)) { preparedStmt ->
            assertEquals(
                "SELECT * FROM dual t WHERE ? IS NULL {1: NULL}",
                preparedStmt.toString().extractQueryFromPreparedStmt()
            )
        }

        withPreparedStmt(queryOf("SELECT * FROM dual t WHERE ? = 1 AND ? IS NULL", 1, null)) { preparedStmt ->
            assertEquals(
                "SELECT * FROM dual t WHERE ? = 1 AND ? IS NULL {1: 1, 2: NULL}",
                preparedStmt.toString().extractQueryFromPreparedStmt()
            )
        }

        withPreparedStmt(
            queryOf(
                "SELECT * FROM dual t WHERE ? = 1 AND ? IS NULL AND ? = 3",
                1,
                null,
                3
            )
        ) { preparedStmt ->
            assertEquals(
                "SELECT * FROM dual t WHERE ? = 1 AND ? IS NULL AND ? = 3 {1: 1, 2: NULL, 3: 3}",
                preparedStmt.toString().extractQueryFromPreparedStmt()
            )
        }
    }

    @Test
    fun nullParamsJdbcHandling() {
        // this test could fail for PostgreSQL
        using(sessionOf(testDataSource)) { session ->

            session.execute(queryOf("drop table if exists members"))
            session.execute(
                queryOf(
                    """
                    create table members (
                      id serial not null primary key
                    )
                    """.trimIndent()
                )
            )
            session.run(queryOf("insert into members(id) values (1)").asUpdate)

            describe("typed param with value") {
                assertEquals(1, session.single(queryOf("select 1 from members where ? = id", 1)) { row -> row.int(1) })
            }

            describe("typed null params") {
                assertEquals(
                    1,
                    session.single(
                        queryOf(
                            "select 1 from members where ? is null",
                            null.param<String>()
                        )
                    ) { row -> row.int(1) })
            }

            describe("typed null comparison") {
                assertEquals(1,
                    session.single(
                        queryOf(
                            "select 1 from members where ? is null or ? = now()",
                            null.param<String>(),
                            null.param<Timestamp>()
                        )
                    ) { row -> row.int(1) }
                )
            }

            describe("select null") {
                val param: String? = null
                assertNull(
                    session.single(
                        queryOf(
                            "select ? from members",
                            Parameter(param, String::class.java)
                        )
                    ) { row -> row.stringOrNull(1) })
            }

            session.run(queryOf("drop table if exists members").asExecute)
        }
    }

    @Test
    fun createAnArray() {
        using(sessionOf(testDataSource)) { session ->

            session.execute(queryOf("drop table if exists members"))
            session.execute(
                queryOf("""
                    create table members (
                        id serial primary key,
                        random_numbers integer array
                    )
                """.trimIndent()
                )
            )
            session.run(
                queryOf(
                    "insert into members(id, random_numbers) values (1, :randomNumbers)",
                    mapOf("randomNumbers" to session.createArrayOf("integer", listOf(1, 2, 3)))
                ).asUpdate
            )

            val readNumbers =
                session.single(queryOf("select random_numbers from members where id = 1")) { it.array<Int>(1) }
            assertEquals(listOf(1, 2, 3), readNumbers?.toList())

            session.run(queryOf("drop table if exists members").asExecute)
        }
    }

    @Test
    fun batchPreparedStatement() {
        using(sessionOf(testDataSource)) { session ->

            val now = Instant.now()
            val res = session.batchPreparedStatement(
                "insert into members(id, created_at) values (?, ?)",
                listOf(listOf(1, now), listOf(2, now), listOf(3, now))
            )

            assertEquals(listOf(1, 1, 1), res)

            val nowAtEpoch = now.epochSecond
            assertEquals(listOf(Pair(1, nowAtEpoch), Pair(2, nowAtEpoch), Pair(3, nowAtEpoch)),
                session.list(queryOf("select id, created_at from members")) { row ->
                    Pair(
                        row.int("id"),
                        row.instant("created_At").epochSecond
                    )
                })
        }
    }

    @Test
    fun batchPreparedNamedStatement() {
        using(sessionOf(testDataSource)) { session ->

            val now = Instant.now()
            val res = session.batchPreparedNamedStatement(
                "insert into members(id, created_at) values (:id, :when)",
                listOf(
                    mapOf("id" to 1, "when" to now),
                    mapOf("id" to 2, "when" to now),
                    mapOf("id" to 3, "when" to now)
                )
            )

            assertEquals(listOf(1, 1, 1), res)
            val nowAtEpoch = now.epochSecond
            assertEquals(listOf(Pair(1, nowAtEpoch), Pair(2, nowAtEpoch), Pair(3, nowAtEpoch)),
                session.list(queryOf("select id, created_at from members")) { row ->
                    Pair(
                        row.int("id"),
                        row.instant("created_At").epochSecond
                    )
                })

        }
    }

    @Test
    fun testStrictMode() {

        val nameQuery = "select id, name, created_at from members where name = ?"

        using(sessionOf(testDataSource, strict = false)) { session ->

            session.update(queryOf(insert, "Alice", Date()))
            session.update(queryOf(insert, "Alice", Date()))

            val lenientAlice: Member? = session.single(queryOf(nameQuery, "Alice"), toMember)
            assertNotNull(lenientAlice)
        }

        using(sessionOf(testDataSource, strict = true)) { session ->
            val exception = assertFailsWith<SQLException> {
                session.single(queryOf(nameQuery, "Alice"), toMember)
            }
            assertEquals(
                "Expected 1 row but received 2.",
                exception.message,
                "Duplicated row is expected to throw exception"
            )
        }
    }

    @Test
    fun testTransactionalStrictMode() {
        using(sessionOf(testDataSource, strict = true)) { session ->

            session.update(queryOf(insert, "Alice", Date()))
            session.update(queryOf(insert, "Alice", Date()))

            val nameQuery = "select id, name, created_at from members where name = ?"

            val exception = assertFailsWith<SQLException> {
                session.transaction { transactionalSession ->
                    transactionalSession.single(queryOf(nameQuery, "Alice"), toMember)
                }
            }
            assertEquals(
                "Expected 1 row but received 2.",
                exception.message,
                "Duplicated row is expected to throw exception"
            )
        }
    }


    private fun withPreparedStmt(query: Query, closure: (PreparedStatement) -> Unit) {
        using(sessionOf(testDataSource)) { session ->

            val preparedStmt = session.createPreparedStatement(query)

            closure(preparedStmt)
        }
    }

    private fun String.extractQueryFromPreparedStmt(): String {
        return this.replace(Regex("^.*?: "), "").normalizeSpaces()
    }

}
