package kotliquery

import kotliquery.action.ExecuteQueryAction
import kotliquery.action.ResultQueryActionBuilder
import kotliquery.action.UpdateQueryAction
import kotliquery.action.UpdateWithKeysQueryAction

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

    val asUpdateWithKeys: UpdateWithKeysQueryAction by lazy {
        UpdateWithKeysQueryAction(this)
    }

    val asExecute: ExecuteQueryAction by lazy {
        ExecuteQueryAction(this)
    }

}