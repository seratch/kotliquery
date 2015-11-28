package kotliquery

import kotliquery.action.ExecuteQueryAction
import kotliquery.action.ResultQueryActionBuilder
import kotliquery.action.UpdateQueryAction

/**
 * Database Query.
 */
data class Query(
        val statement: String,
        val params: List<Any>) {

    fun map<A>(extractor: (Row) -> A?): ResultQueryActionBuilder<A> {
        return ResultQueryActionBuilder(this, extractor)
    }

    val asUpdate: UpdateQueryAction by lazy {
        UpdateQueryAction(this)
    }

    val asExecute: ExecuteQueryAction by lazy {
        ExecuteQueryAction(this)
    }

}