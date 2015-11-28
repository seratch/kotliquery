package kotliquery

import java.sql.DriverManager

/**
 * Builds Query object.
 */
fun queryOf(statement: String, vararg params: Any): Query {
    return Query(statement, params.toList())
}

/**
 * Builds Session object.
 */
fun sessionOf(url: String, user: String, password: String): Session {
    val conn = DriverManager.getConnection(url, user, password)
    return Session(Connection(conn))
}