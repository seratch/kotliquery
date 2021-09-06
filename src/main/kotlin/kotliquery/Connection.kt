package kotliquery

import java.sql.SQLException

/**
 * Database Connection.
 */
data class Connection(
    val underlying: java.sql.Connection,
    val driverName: String = "",
    val jtaCompatible: Boolean = false
) {

    fun begin(): Unit {
        underlying.autoCommit = false
        if (jtaCompatible == false) {
            underlying.isReadOnly = false
        }
    }

    fun commit(): Unit {
        underlying.commit()
        underlying.autoCommit = true
    }

    fun rollback(): Unit {
        underlying.rollback()
        try {
            underlying.autoCommit = true
        } catch (e: SQLException) {
        }
    }

    fun close(): Unit {
        underlying.close()
    }


}