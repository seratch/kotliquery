import kotliquery.queryOf
import kotliquery.sessionOf

class Example {

    val session = sessionOf("jdbc:h2:mem:hello", "user", "pass")

    fun init(): Unit {
        session.run(queryOf("""
  create table members (
    id serial not null primary key,
    name varchar(64),
    created_at timestamp not null
  )
""").asExecute)
        val insertQuery: String = "insert into members (name,  created_at) values (?, ?)"
        listOf("Alice", "Bob").forEach { name ->
            session.run(queryOf(insertQuery, name, java.time.ZonedDateTime.now()).asUpdate)
        }
    }

}
