package kotliquery

/**
 * Transactional Database session.
 */
class TransactionalSession(
    override val connection: Connection,
    override val returnGeneratedKeys: Boolean = true,
    override val autoGeneratedKeys: List<String> = listOf(),
    override val strict: Boolean = false
) : Session(connection, returnGeneratedKeys, autoGeneratedKeys, strict = strict) {}