import kotliquery.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExampleTest {

    val example = Example()

    val toMember: (Row) -> Member = { row ->
        Member(row.int("id"), row.stringOrNull("name"), row.zonedDateTime("created_at"))
    }

    @Before
    fun prepare() {
        example.init()
    }

    @Test
    fun sample() {
        val db = example.session
        val members: List<Member> = db.run(queryOf("select id, name, created_at from members").map(toMember).asList)
        assertEquals(2, members.size)
    }

}
