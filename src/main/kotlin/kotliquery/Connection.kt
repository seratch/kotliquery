package kotliquery

/**
 * Database Connection.
 */
data class Connection(
        val underlying: java.sql.Connection,
        val driverName: String)