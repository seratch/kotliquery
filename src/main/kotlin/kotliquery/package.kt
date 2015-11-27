package kotliquery

/**
 * Builds Query object.
 */
fun queryOf(statement: String, vararg params: Any): Query {
    return Query(statement, params.toList())
}