package kotliquery.action

import kotliquery.Query
import kotliquery.Row
import kotliquery.Session

data class NullableResultQueryAction<A>(
        val query: Query,
        val extractor: (Row) -> A?) : QueryAction<A?> {

    override fun runWithSession(session: Session): A? {
        return session.single(query, extractor)
    }
}