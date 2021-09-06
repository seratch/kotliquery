package kotliquery.action

import kotliquery.Query
import kotliquery.Row
import kotliquery.Session

data class ListResultQueryAction<A>(
    val query: Query,
    val extractor: (Row) -> A?
) : QueryAction<List<A>> {

    override fun runWithSession(session: Session): List<A> {
        return session.list(query, extractor)
    }
}