package kotliquery.action

import kotliquery.Session

interface QueryAction<A> {

    fun runWithSession(session: Session): A

}