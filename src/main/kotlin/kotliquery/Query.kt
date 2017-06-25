package kotliquery

import kotliquery.action.ExecuteQueryAction
import kotliquery.action.ResultQueryActionBuilder
import kotliquery.action.UpdateQueryAction
import kotliquery.action.UpdateAndReturnGeneratedKeyQueryAction

/**
 * Database Query.
 */
data class Query(
        val statement: String,
        val params: List<Any?>) {

    fun <A> map(extractor: (Row) -> A?): ResultQueryActionBuilder<A> {
        return ResultQueryActionBuilder(this, extractor)
    }

    val asUpdate: UpdateQueryAction by lazy {
        UpdateQueryAction(this)
    }

    val asUpdateAndReturnGeneratedKey : UpdateAndReturnGeneratedKeyQueryAction by lazy {
        UpdateAndReturnGeneratedKeyQueryAction(this)
    }

    val asExecute: ExecuteQueryAction by lazy {
        ExecuteQueryAction(this)
    }

}