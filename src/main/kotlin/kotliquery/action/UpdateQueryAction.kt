package kotliquery.action

import kotliquery.Query
import kotliquery.Session

data class UpdateQueryAction(val query: Query) : QueryAction<Int> {

    override fun runWithSession(session: Session): Int {
        return session.update(query)
    }
}