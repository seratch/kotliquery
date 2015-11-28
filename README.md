## kotliquery

[![Build Status](https://travis-ci.org/seratch/kotliquery.svg)](https://travis-ci.org/seratch/kotliquery)

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

val toMember: (Row) -> Member = {
  row -> Member(row.int("id")!!, row.string("name"), row.sqlTimestamp("created_at")!!)
}

val conn = DriverManager.getConnection("jdbc:h2:mem:hello", "user", "pass")
val session = Session(Connection(conn, "org.h2.Driver"))

session.run(queryOf("""
  create table members (
    id serial not null primary key,
    name varchar(64),
    created_at timestamp not null
  )
""").asExecute)

session.run(queryOf("insert into members (name,  created_at) values (?, ?)", "Alice", Date()).asUpdate)
session.run(queryOf("insert into members (name,  created_at) values (?, ?)", "Bob", Date()).asUpdate)

val allIdsQuery = queryOf("select id from members").map { row -> row.int("id") }.asList
val ids: List<Int> = session.run(allIdsQuery)

session.forEach(queryOf("select id from members"), { row -> 
  // working with large result sets
})

val allMembersQuery = queryOf("select id, name, created_at from members").map(toMember).asList
val members: List<Member> = session.run(allMembersQuery)

val aliceQuery = queryOf("select id, name, created_at from members where name = ?", "Alice").map(toMember).asSingle
val alice: Member? = session.run(aliceQuery)
```

## License

(The MIT License)

Copyright (c) 2015 Kazuhiro Sera
