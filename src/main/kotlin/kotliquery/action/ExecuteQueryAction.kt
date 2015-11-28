package kotliquery.action

import kotliquery.Query
import kotliquery.Session

data class ExecuteQueryAction(val query: Query) : QueryAction<Boolean> {

    override fun runWithSession(session: Session): Boolean {
        return session.execute(query)
    }
}