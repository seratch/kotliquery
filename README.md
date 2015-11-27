## kotliquery

A handy Database access library in Kotlin. Highly inspired from [ScalikeJDBC](http://scalikejdbc.org/).

### Example

```kotlin
import java.sql.DriverManager
import java.util.*
import kotliquery.*

data class Member(
  val id: Int,
  val name: String?,
  val createdAt: Date)

val conn = DriverManager.getConnection("jdbc:h2:mem:hello", "user", "pass")
val session = Session(Connection(conn, "org.h2.Driver"))

session.execute(queryOf("""
  create table members (
    id serial not null primary key,
    name varchar(64),
    created_at timestamp not null
  )
"""))

session.update(queryOf("insert into members (name,  created_at) values (?, ?)", "Alice", Date()))
session.update(queryOf("insert into members (name,  created_at) values (?, ?)", "Bob", Date()))

val ids: List<Int> = session.list(queryOf("select id from members"), { row -> row.int("id") })

session.forEach(queryOf("select id from members"), { row -> 
  // working with large result sets
})

val members: List<Member> = session.list(queryOf("select id, name, created_at from members"), { row ->
    Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
})

val nameQuery = "select id, name, created_at from members where name = ?"
val alice: Member? = session.single(queryOf(nameQuery, "Alice"), { row ->
    Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
})
```

## License

(The MIT License)

Copyright (c) 2015 Kazuhiro Sera
