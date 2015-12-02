package kotliquery

/**
 * Loan Pattern idiom.
 */
object LoanPattern {

    fun <A : AutoCloseable, R> using(closeable: A?, f: (A) -> R): R {
        try {
            if (closeable != null) {
                return f(closeable)
            } else {
                throw IllegalStateException("Closeable resource is unexpectedly null.")
            }
        } finally {
            closeable?.close()
        }
    }

}
