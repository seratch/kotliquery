package kotliquery

import java.sql.DriverManager
import javax.sql.DataSource

/**
 * Builds Query object.
 */
fun queryOf(statement: String, vararg params: Any?): Query {
    return Query(statement, params = params.toList())
}

fun queryOf(statement: String, paramMap: Map<String, Any?>): Query {
    return Query(statement, paramMap = paramMap)
}

/**
 * Builds Session object.
 */
fun sessionOf(
    url: String,
    user: String,
    password: String,
    returnGeneratedKey: Boolean = false,
    strict: Boolean = false
): Session {
    val conn = DriverManager.getConnection(url, user, password)
    return Session(Connection(conn), returnGeneratedKey, strict = strict)
}

fun sessionOf(dataSource: DataSource, returnGeneratedKey: Boolean = false, strict: Boolean = false): Session {
    return Session(Connection(dataSource.connection), returnGeneratedKey, strict = strict)
}

fun <A : AutoCloseable, R> using(closeable: A?, f: (A) -> R): R {
    return LoanPattern.using(closeable, f)
}
