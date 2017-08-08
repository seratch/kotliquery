package kotliquery

import org.junit.Test
import kotlin.test.assertEquals

class NamedParamTest {

    @Test
    fun paramExtraction() {
        describe("should extract a single param") {
            withQueries(
                    "SELECT * FROM table t WHERE t.a = :param",
                    """SELECT * FROM table t WHERE t.a =
                :param"""
            ) { query ->
                assertEquals("SELECT * FROM table t WHERE t.a = ?", query.cleanStatement.normalizeSpaces())
                assertEquals(mapOf("param" to listOf(0)), query.replacementMap)
            }
        }

        describe("should extract multiple params") {
            withQueries(
                    "SELECT * FROM table t WHERE t.a = :param1 AND t.b = :param2"
            ) { query ->
                assertEquals("SELECT * FROM table t WHERE t.a = ? AND t.b = ?", query.cleanStatement)
                assertEquals(mapOf(
                        "param1" to listOf(0),
                        "param2" to listOf(1)
                ), query.replacementMap)
            }
        }

        describe("should extract multiple repeated params") {
            withQueries(
                    """SELECT * FROM table t WHERE (:param1 IS NULL OR t.a = :param2)
                 AND (:param2 IS NULL OR t.b = :param3)
                 AND (:param3 IS NULL OR t.c = :param1)"""
            ) { query ->
                assertEquals(
                        "SELECT * FROM table t WHERE (? IS NULL OR t.a = ?) AND (? IS NULL OR t.b = ?) AND (? IS NULL OR t.c = ?)", query.cleanStatement.normalizeSpaces()
                        )
                assertEquals(mapOf(
                        "param1" to listOf(0, 5),
                        "param2" to listOf(1, 2),
                        "param3" to listOf(3, 4)
                ), query.replacementMap)
            }
        }
    }

    fun describe(description: String, tests: () -> Unit) {
        println(description)

        tests()
    }

    fun withQueries(vararg stmts: String, assertions: (Query) -> Unit) {
        stmts.forEach {
            val query = queryOf(it)

            assertions(query)
        }
    }

}

fun String.normalizeSpaces(): String {
    return Regex("[ \\n\\t]+").replace(this, " ")
}