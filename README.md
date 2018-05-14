## KotliQuery

------------

**This was forked from: https://github.com/seratch/kotliquery**
So far I have not made any changes. I am using this library so I want a copy of the source. 
It seems to work well - I'll send PRs if I fix an issue or extend it in any useful way.

------------


[![Build Status](https://travis-ci.org/seratch/kotliquery.svg)](https://travis-ci.org/seratch/kotliquery)

A handy RDB client library in Kotlin. Highly inspired from [ScalikeJDBC](http://scalikejdbc.org/). This library focuses on providing handy and Kotlin-ish API to issue a query and extract values from its JDBC ResultSet iterator.

### Getting Started

You can try this library with Gradle right now. See the sample project:

https://github.com/seratch/kotliquery/tree/master/sample

#### build.gradle

```groovy
apply plugin: 'kotlin'

buildscript {
    ext.kotlin_version = '1.2.21'
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
repositories {
    mavenCentral()
}
dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    compile 'com.github.seratch:kotliquery:1.2.0'
    compile 'com.h2database:h2:1.4.196'
    compile 'com.zaxxer:HikariCP:2.7.7'
}
```

### Example

KotliQuery is very easy-to-use. After reading this short documentation, you will have learnt enough.

#### Creating DB Session

`Session` object, thin wrapper of `java.sql.Connection` instance, runs queries.

```kotlin
import kotliquery.*

val session = sessionOf("jdbc:h2:mem:hello", "user", "pass") 
```

#### HikariCP

Using connection pool would be better for serious programming.

[HikariCP](https://github.com/brettwooldridge/HikariCP) is blazingly fast and so handy.

```kotlin
HikariCP.default("jdbc:h2:mem:hello", "user", "pass")

using(sessionOf(HikariCP.dataSource())) { session ->
   // working with the session
}
```

#### DDL Execution

```kotlin
session.run(queryOf("""
  create table members (
    id serial not null primary key,
    name varchar(64),
    created_at timestamp not null
  )
""").asExecute) // returns Boolean
```

#### Update Operations

```kotlin
val insertQuery: String = "insert into members (name,  created_at) values (?, ?)"

session.run(queryOf(insertQuery, "Alice", Date()).asUpdate) // returns effected row count
session.run(queryOf(insertQuery, "Bob", Date()).asUpdate)
```

#### Select Queries

Prepare select query execution in the following steps:

- Create `Query` object by using `queryOf` factory
- Bind extractor function (`(Row) -> A`) to the `Query` object via `#map` method
- Specify response type (`asList`/`asSingle`) at the end

```kotlin
val allIdsQuery = queryOf("select id from members").map { row -> row.int("id") }.asList
val ids: List<Int> = session.run(allIdsQuery)
```

Extractor function can return any type of result from `ResultSet`.

```kotlin
data class Member(
  val id: Int,
  val name: String?,
  val createdAt: java.time.ZonedDateTime)

val toMember: (Row) -> Member = { row -> 
  Member(
    row.int("id"), 
    row.stringOrNull("name"), 
    row.zonedDateTime("created_at")
  )
}

val allMembersQuery = queryOf("select id, name, created_at from members").map(toMember).asList
val members: List<Member> = session.run(allMembersQuery)
```

```kotlin
val aliceQuery = queryOf("select id, name, created_at from members where name = ?", "Alice").map(toMember).asSingle
val alice: Member? = session.run(aliceQuery)
```

#### Named query parameters

Alternative syntax is supported to allow named parameters in all queries. 

```kotlin
queryOf("""select id, name, created_at 
	from members 
	where (:name is not null or name = :name)
	  and (:age is not null or age = :age)""", 
	mapOf("name" to "Alice"))
```

In the query above, the param `age` is not supplied on purpose.

Performance-wise this syntax is slightly slower to prepare the statement and a tiny bit more memory-consuming, due to param mapping. Use it if readability is a priority.

Importantly, this method is not based on "artificial" string replacement. In fact, the statement is prepared just as if it was the default syntax.

#### Typed params

In the case, the parameter type has to be explicitly stated, there's a wrapper class - `Parameter` that will help provide explicit type information.

```kotlin
val param = Parameter(param, String::class.java)
queryOf("""select id, name 
    from members 
    where ? is null or ? = name""", 
    param, param)
``` 

or also with the helper function `param`

```kotlin
queryOf("""select id, name 
    from members 
    where ? is null or ? = name""", 
    null.param<String>(), null.param<String>())
```

This can be useful in situations similar to one described [here](https://www.postgresql.org/message-id/6ekbd7dm4d6su5b9i4hsf92ibv4j76n51f@4ax.com).

#### Working with Large Dataset

`#forEach` allows you to make some side-effect in iterations. This API is useful for handling large `ResultSet`.

```kotlin
session.forEach(queryOf("select id from members")) { row ->
  // working with large data set
})
```

#### Transaction

`Session` object provides transaction block.

```kotlin
session.transaction { tx ->
  // begin
  tx.run(queryOf("insert into members (name,  created_at) values (?, ?)", "Alice", Date()).asUpdate)
}
// commit

session.transaction { tx ->
  // begin
  tx.run(queryOf("update members set name = ? where id = ?", "Chris", 1).asUpdate)
  throw RuntimeException() // rollback
}
```

## License

(The MIT License)

Copyright (c) 2015 - Kazuhiro Sera
