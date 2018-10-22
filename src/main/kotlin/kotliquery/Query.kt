package kotliquery

import kotliquery.action.ExecuteQueryAction
import kotliquery.action.ResultQueryActionBuilder
import kotliquery.action.UpdateAndReturnGeneratedKeyQueryAction
import kotliquery.action.UpdateQueryAction

/**
 * Database Query.
 */
data class Query(
        val statement: String,
        val params: List<Any?> = listOf(),
        val paramMap: Map<String, Any?> = mapOf()) {

    val replacementMap: Map<String, List<Int>> = extractNamedParamsIndexed(statement)
    val cleanStatement: String = replaceNamedParams(statement)

    private fun extractNamedParamsIndexed(stmt: String): Map<String, List<Int>> {
        return regex.findAll(stmt).mapIndexed { index, group ->
            Pair(group, index)
        }.groupBy({ it.first.value.substring(1) }, { it.second })
    }

    private fun replaceNamedParams(stmt: String): String {
        return regex.replace(stmt, "?")
    }

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

    companion object {
        private val regex = Regex("""(?<!:):(?!:)\w+""")
    }
}
