package kotliquery

import org.junit.Test
import java.sql.DriverManager
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UsageTest {

    data class Member(
            val id: Int,
            val name: String?,
            val createdAt: Date)

    @Test
    fun simpleExample() {

        val conn = DriverManager.getConnection("jdbc:h2:mem:hello", "user", "pass")
        val session = Session(Connection(conn, "org.h2.Driver"))

        session.execute(queryOf("""
create table members (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
        """))
        session.update(queryOf("insert into members (name,  created_at) values (?, ?)", "Alice", Date()))
        session.update(queryOf("insert into members (name,  created_at) values (?, ?)", "Bob", Date()))

        val ids: List<Int> = session.list(queryOf("select id from members"), { row -> row.int("id") })
        assertEquals(2, ids.size)

        val members: List<Member> = session.list(queryOf("select id, name, created_at from members"), { row ->
            Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
        })
        assertEquals(2, members.size)

        var count = 0
        session.forEach(queryOf("select id from members"), { row ->
            count++
            assertNotNull(row.int("id"))
        })
        assertEquals(2, count)

        val nameQuery = "select id, name, created_at from members where name = ?"
        val alice: Member? = session.single(queryOf(nameQuery, "Alice"), { row ->
            Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
        })
        assertNotNull(alice)

        val bob: Member? = session.single(queryOf(nameQuery, "Bob"), { row ->
            Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
        })
        assertNotNull(bob)

        val chris: Member? = session.single(queryOf(nameQuery, "Chris"), { row ->
            Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
        })
        assertNull(chris)
    }

}