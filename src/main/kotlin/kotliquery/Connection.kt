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

    fun begin() {
        underlying.autoCommit = false
        if (!jtaCompatible) {
            underlying.isReadOnly = false
        }
    }

    fun commit() {
        underlying.commit()
        underlying.autoCommit = true
    }

    fun rollback() {
        underlying.rollback()
        try {
            underlying.autoCommit = true
        } catch (e: SQLException) {
        }
    }

    fun close() {
        underlying.close()
    }

}