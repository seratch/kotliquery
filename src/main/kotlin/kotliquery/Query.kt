package kotliquery

/**
 * Database Query.
 */
data class Query(
        val statement: String,
        val params: List<Any>)