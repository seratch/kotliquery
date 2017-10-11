package kotliquery

import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Types
import java.time.*

open class Parameter<out T>(val value: T?,
                            val type: Class<out T>)

fun <T> Parameter<T>.sqlType() = when (type) {
    String::class.java, URL::class.java -> Types.VARCHAR
    Int::class.java, Long::class.java, Short::class.java,
    Byte::class.java, BigInteger::class.java -> Types.NUMERIC
    Double::class.java, BigDecimal::class.java -> Types.DOUBLE
    Float::class.java -> Types.FLOAT
    java.sql.Date::class.java, java.util.Date::class.java, ZonedDateTime::class.java, OffsetDateTime::class.java,
    Instant::class.java, LocalDateTime::class.java, org.joda.time.DateTime::class.java, org.joda.time.LocalDateTime::class.java,
    java.sql.Timestamp::class.java -> Types.TIMESTAMP
    java.sql.Time::class.java, LocalTime::class.java -> Types.TIME
    LocalDate::class.java -> Types.DATE
    else -> Types.OTHER
}

inline fun <reified T> T?.param(): Parameter<T> = when (this) {
    is Parameter<*> -> Parameter(this.value as T?, this.type as Class<T>)
    else -> Parameter(this, T::class.java)
}