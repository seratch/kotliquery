## KotliQuery

[![Build Status](https://travis-ci.org/seratch/kotliquery.svg)](https://travis-ci.org/seratch/kotliquery)

A handy Database access library in Kotlin. Highly inspired from [ScalikeJDBC](http://scalikejdbc.org/). 

Kotlin language is still beta, and this library is also still in beta stage.
When Kotlin 1.0 release, we'll consider releasing the first version of KotliQuery.

### Getting Started

You can try this library with Gradle right now. See the sample project:

https://github.com/seratch/kotliquery/tree/master/sample

#### build.gradle

```groovy
apply plugin: 'kotlin'

buildscript {
    ext.kotlin_version = '1.0.0-beta-2423'
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
repositories {
    mavenCentral()
    maven { url 'http://oss.sonatype.org/content/repositories/snapshots' }
}
dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    compile 'com.github.seratch:kotlin-query:0.1.0-SNAPSHOT'
    compile 'com.h2database:h2:1.4.190'
    compile 'com.zaxxer:HikariCP:2.4.2'
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
  Member(row.int("id")!!, row.string("name"), row.zonedDateTime("created_at")!!)
}

val allMembersQuery = queryOf("select id, name, created_at from members").map(toMember).asList
val members: List<Member> = session.run(allMembersQuery)
```

```kotlin
val aliceQuery = queryOf("select id, name, created_at from members where name = ?", "Alice").map(toMember).asSingle
val alice: Member? = session.run(aliceQuery)
```

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

Copyright (c) 2015 Kazuhiro Sera
