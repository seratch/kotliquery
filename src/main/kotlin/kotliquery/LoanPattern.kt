package kotliquery

/**
 * Loan Pattern idiom.
 */
interface LoanPattern {

    fun <A : AutoCloseable, R> using(s: A, f: (A) -> R): R {
        try {
            return f(s)
        } finally {
            s.close()
        }
    }

}
