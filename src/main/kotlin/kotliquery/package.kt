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
fun sessionOf(url: String, user: String, password: String, returnGeneratedKey: Boolean = false): Session {
    val conn = DriverManager.getConnection(url, user, password)
    return Session(Connection(conn), returnGeneratedKey)
}

fun sessionOf(dataSource: DataSource, returnGeneratedKey: Boolean = false): Session {
    return Session(Connection(dataSource.connection), returnGeneratedKey)
}

fun <T> sessionOf(dataSource: DataSource, returnGeneratedKey: Boolean = false, operation: (Session) -> T): T {
    return using(sessionOf(dataSource, returnGeneratedKey)) {
        operation(it)
    }
}

fun <T> transactionOf(dataSource: DataSource, returnGeneratedKey: Boolean = false, operation: (TransactionalSession) -> T): T {
    return using(sessionOf(dataSource, returnGeneratedKey)) {
        it.transaction(operation)
    }
}

fun <A : AutoCloseable, R> using(closeable: A?, f: (A) -> R): R {
    return LoanPattern.using(closeable, f)
}
