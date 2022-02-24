package kotliquery

import org.joda.time.chrono.ISOChronology
import org.junit.Before
import org.junit.Test
import java.time.*
import java.util.*
import kotlin.test.assertEquals

/*
 * Many popular DBs store "TIMESTAMP WITH TIME ZONE" as epoch time (8 bytes) without timezone info.
 *    https://www.postgresql.org/docs/14/datatype-datetime.html
 *    https://dev.mysql.com/doc/refman/8.0/en/datetime.html
 *
 * Opposite (where timezone is stored) also exist:
 *    https://docs.oracle.com/cd/E11882_01/server.112/e10729/ch4datetime.htm#i1006081
 *
 * This lib supports only epoch time without timezone info
 */
class DataTypesTest {

    @Before
    fun before() {
        sessionOf(testDataSource).use { session ->
            session.execute(
                queryOf(
                    """
                    drop table session_test if exists;
                    
                    create table session_test (
                        val_tstz timestamp with time zone,
                        val_ts timestamp,
                        val_date date,
                        val_time time,
                        val_uuid uuid
                    );
                    """.trimIndent()
                )
            )
        }
    }

    private fun insertAndAssert(
        tstz: Any? = null,
        ts: Any? = null,
        date: Any? = null,
        time: Any? = null,
        uuid: Any? = null,
        assertRowFn: (Row) -> Unit
    ) {
        sessionOf(testDataSource).use { session ->
            session.execute(queryOf("truncate table session_test;"))

            session.execute(
                queryOf(
                    """
                    insert into session_test (val_tstz, val_ts, val_date, val_time, val_uuid) 
                        values (:tstz, :ts, :date, :time, :uuid);
                """.trimIndent(),
                    mapOf(
                        "tstz" to tstz,
                        "ts" to ts,
                        "date" to date,
                        "time" to time,
                        "uuid" to uuid
                    )
                )
            )

            session.single(queryOf("select * from session_test"), assertRowFn)
        }
    }

    @Test
    fun testZonedDateTime() {
        val value = ZonedDateTime.parse("2010-01-01T10:00:00.123456+01:00[Europe/Berlin]")
        insertAndAssert(tstz = value) { row ->
            assertEquals(
                value.withZoneSameInstant(ZoneId.systemDefault()),
                row.zonedDateTime("val_tstz")
            )
        }

        val tstz2 = ZonedDateTime.parse("2010-06-01T10:00:00.234567-07:00[America/Los_Angeles]") // DST
        insertAndAssert(tstz = tstz2) { row ->
            assertEquals(
                tstz2.withZoneSameInstant(ZoneId.systemDefault()),
                row.zonedDateTime("val_tstz")
            )
        }
    }

    @Test
    fun testOffsetDateTime() {
        val value = OffsetDateTime.parse("2010-01-01T10:00:00.123456+01:00")
        insertAndAssert(tstz = value) { row ->
            assertEquals(
                value.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime(),
                row.offsetDateTime("val_tstz")
            )
        }

        val value2 = OffsetDateTime.parse("2010-06-01T10:00:00.234567-07:00") // DST
        insertAndAssert(tstz = value2) { row ->
            assertEquals(
                value2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime(),
                row.offsetDateTime("val_tstz")
            )
        }
    }

    @Test
    fun testInstant() {
        val value = Instant.parse("2010-01-01T10:00:00.123456Z")
        insertAndAssert(tstz = value) { row ->
            assertEquals(value, row.instant("val_tstz"))
        }
    }

    @Test
    fun testLocalDateTime() {
        val value = LocalDateTime.parse("2010-01-01T10:00:00.123456")
        insertAndAssert(ts = value) { row ->
            assertEquals(value, row.localDateTime("val_ts"))
        }
    }

    @Test
    fun testLocalDate() {
        val value = LocalDate.parse("2010-01-01")
        insertAndAssert(date = value) { row ->
            assertEquals(value, row.localDate("val_date"))
        }
    }

    @Test
    fun testLocalTime() {
        val value = LocalTime.parse("10:00:00")
        insertAndAssert(time = value) { row ->
            assertEquals(value, row.localTime("val_time"))
        }
    }

    @Test
    fun testJodaDateTime() {
        val value = org.joda.time.DateTime.parse("2010-01-01T10:00:00.123+01:00")
        insertAndAssert(tstz = value) { row ->
            assertEquals(
                org.joda.time.DateTime(value, ISOChronology.getInstance()),
                row.jodaDateTime("val_tstz")
            )
        }

        val value2 = org.joda.time.DateTime.parse("2010-06-01T10:00:00.234-07:00") // DST
        insertAndAssert(tstz = value2) { row ->
            assertEquals(
                org.joda.time.DateTime(value2, ISOChronology.getInstance()),
                row.jodaDateTime("val_tstz")
            )
        }
    }

    @Test
    fun testJodaLocalDateTime() {
        val value = org.joda.time.LocalDateTime.parse("2010-01-01T10:00:00.123456")
        insertAndAssert(ts = value) { row ->
            assertEquals(value, row.jodaLocalDateTime("val_ts"))
        }
    }

    @Test
    fun testJodaLocalDate() {
        val value = org.joda.time.LocalDate.parse("2010-01-01")
        insertAndAssert(date = value) { row ->
            assertEquals(value, row.jodaLocalDate("val_date"))
        }
    }

    @Test
    fun testJodaLocalTime() {
        val value = org.joda.time.LocalTime.parse("10:00:00")
        insertAndAssert(time = value) { row ->
            assertEquals(value, row.jodaLocalTime("val_time"))
        }
    }

    @Test
    fun testSqlTimestamp() {
        val value = java.sql.Timestamp.valueOf("2010-01-01 10:00:00.123456")
        insertAndAssert(tstz = value) { row ->
            assertEquals(value, row.sqlTimestamp("val_tstz"))
        }
    }

    @Test
    fun testSqlDate() {
        val value = java.sql.Date.valueOf("2010-01-01")
        insertAndAssert(date = value) { row ->
            assertEquals(value, row.sqlDate("val_date"))
        }
    }

    @Test
    fun testSqlTime() {
        val value = java.sql.Time.valueOf("10:00:00")
        insertAndAssert(time = value) { row ->
            assertEquals(value, row.sqlTime("val_time"))
        }
    }

    @Test
    fun testUuid() {
        val value = UUID.fromString("44ebc207-bb46-401c-8487-62504e1c3be2")
        insertAndAssert(uuid = value) { row ->
            assertEquals(value, row.uuid("val_uuid"))
        }
    }
}