package kotliquery

import java.sql.DriverManager
import javax.sql.DataSource

/**
 * Builds Query object.
 */
fun queryOf(statement: String, vararg params: Any): Query {
    return Query(statement, params.toList())
}

/**
 * Builds Session object.
 */
fun sessionOf(url: String, user: String, password: String, returnGenKeys: Boolean = false): Session {
    val conn = DriverManager.getConnection(url, user, password)
    return Session(Connection(conn), returnGenKeys)
}

fun sessionOf(dataSource: DataSource, returnGenKeys: Boolean = false): Session {
    return Session(Connection(dataSource.connection), returnGenKeys)
}

fun <A : AutoCloseable, R> using(closeable: A?, f: (A) -> R): R {
    return LoanPattern.using(closeable, f)
}
