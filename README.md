## KotliQuery

[![CI Builds](https://github.com/seratch/kotliquery/actions/workflows/ci-builds.yml/badge.svg)](https://github.com/seratch/kotliquery/actions/workflows/ci-builds.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.seratch/kotliquery.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.seratch%22%20AND%20a:%22kotliquery%22)

KotliQuery is a handy RDB client library for Kotlin developers! The design is highly inspired by [ScalikeJDBC](http://scalikejdbc.org/), which is a proven database library in Scala. The priorities in this project are:

* Less learning time
* No breaking changes in releases
* No additional complexity on top of JDBC

This library simply mitigates some pain points of the JDBC but our goal is not to completely encapsulate it.

### Getting Started

The quickest way to try this library out would be to start with a simple Gradle project. You can find some examples [here](https://github.com/seratch/kotliquery/tree/master/sample).

#### build.gradle

```groovy
apply plugin: 'kotlin'

buildscript {
    ext.kotlin_version = '1.5.30'
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
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'com.github.seratch:kotliquery:1.6.0'
    implementation 'com.h2database:h2:1.4.200'
    implementation 'com.zaxxer:HikariCP:4.0.3'
}
```

### Example

KotliQuery is more easy-to-use than you expect. Just after reading this short section, you will have learnt enough.

#### Creating DB Session

First thing you do is to create a `Session` object, which is a thin wrapper of `java.sql.Connection` instance. With this object, you can run queries using an established database connection.

```kotlin
import kotliquery.*

val session = sessionOf("jdbc:h2:mem:hello", "user", "pass") 
```

#### HikariCP

For production-grade applications, utilizing a connection pool library for better connection management is generally recommended. KotliQuery provides an out-of-the-box solution that leverages [HikariCP](https://github.com/brettwooldridge/HikariCP), which is a widely accepted connection pool library.

```kotlin
HikariCP.default("jdbc:h2:mem:hello", "user", "pass")

using(sessionOf(HikariCP.dataSource())) { session ->
   // working with the session
}
```

#### DDL Execution

You acn use a session for executing both DDLs and DMLs. The `asExecute` method if a query object sets the underlying JDBC Statement method to [`execute`](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#execute-java.lang.String-).

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

For insert/update/delete statements, using `asUpdate` is appropriate. This method sets the underlying JDBC Statement method to [executeUpdate](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#executeUpdate-java.lang.String-). 

```kotlin
val insertQuery: String = "insert into members (name,  created_at) values (?, ?)"

session.run(queryOf(insertQuery, "Alice", Date()).asUpdate) // returns effected row count
session.run(queryOf(insertQuery, "Bob", Date()).asUpdate)
```

#### Select Queries

Now you've got a database table named `members`! Let's run your first SQL statement with this library. To run a query, your code follows the three steps as below:

- Create a `Query` object by using `queryOf` factory method
- Attach an extractor function (`(Row) -> A`) to the `Query` object via `#map` method
- Specify the response type (`asList`/`asSingle`) for the result

```kotlin
val allIdsQuery = queryOf("select id from members").map { row -> row.int("id") }.asList
val ids: List<Int> = session.run(allIdsQuery)
```

An extractor function can return any type of result from underlying JDBC `ResultSet` iterator.

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

An alternative way to bind parameters is to use named parameters that start with `:` in the statement string. Note that, with this feature, KotliQuery still uses a prepared statement internally and your query execution is safe from SQL injection. The parameter parts like `:name` and `:age` in the following example query won't be just replaced as string values.

```kotlin
queryOf("""select id, name, created_at 
	from members 
	where (:name is not null or name = :name)
	  and (:age is not null or age = :age)""", 
	mapOf("name" to "Alice", "age" to 20))
```

Performance-wise, the named parameter syntax can be slightly slower for parsing the statement plus a tiny bit more memory-consuming. But for most use case, the overhead should be ignorable. If you would like to make your SQL statements more readable and/or if your query has to repeat the same parameter in a query, using named query parameters should improve your productivity and the maintainability of the query a lot.

#### Typed params

You can specify the Java type for each parameter in the following way. Passing the class `Parameter` helps KotliQuery properly determine the type to bind for each parameter in queries.

```kotlin
val param = Parameter(param, String::class.java)
queryOf("""select id, name 
    from members 
    where ? is null or ? = name""", 
    param, param)
``` 

As a handier way, you can use the following helper method.

```kotlin
queryOf("""select id, name 
    from members 
    where ? is null or ? = name""", 
    null.param<String>(), null.param<String>())
```

This functionality is particularly useful in the situations like [the ones dsecribed here](https://www.postgresql.org/message-id/6ekbd7dm4d6su5b9i4hsf92ibv4j76n51f@4ax.com).

#### Working with Large Dataset

The `#forEach` allows you to work with each row with less memory consumption. With this way, your application code does not need to load all the query result data in memory at once. This feature is greatly useful when you load a large number of rows from a database table by a single query.

```kotlin
session.forEach(queryOf("select id from members")) { row ->
  // working with large data set
})
```

#### Transaction

Running queries in a transaction is of course supported! The `Session` object provides a way to start a transaction in a certain code block. As this library is a bit opinionated, transactions are available only with a code block. We intentionally do not support `begin` / `commit` methods.

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
