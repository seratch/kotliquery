package kotliquery.action

import kotliquery.Query
import kotliquery.Row

data class ResultQueryActionBuilder<A>(
    val query: Query,
    val extractor: (Row) -> A?
) {

    val asList: ListResultQueryAction<A> by lazy {
        ListResultQueryAction(query, extractor)
    }

    val asSingle: NullableResultQueryAction<A> by lazy {
        NullableResultQueryAction(query, extractor)
    }
}
