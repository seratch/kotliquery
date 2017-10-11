package kotliquery


fun describe(description: String, tests: () -> Unit) {
    println(description)

    tests()
}

fun withQueries(vararg stmts: String, assertions: (Query) -> Unit) {
    stmts.forEach {
        val query = queryOf(it)

        assertions(query)
    }
}

fun String.normalizeSpaces(): String {
    return Regex("[ \\n\\t]+").replace(this, " ")
}