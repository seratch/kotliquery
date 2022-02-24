package kotliquery

import org.joda.time.DateTime
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.time.*

/**
 * Represents ResultSet and its each row.
 */
data class Row(
    val underlying: ResultSet,
    val cursor: Int = 0
) : Sequence<Row> {

    private class RowIterator(val rs: ResultSet, val position: Int) : Iterator<Row> {
        override fun next(): Row {
            return Row(rs, position + 1)
        }

        override fun hasNext(): Boolean {
            return rs.isClosed == false && rs.next()
        }
    }

    override fun iterator(): Iterator<Row> {
        return RowIterator(underlying, cursor)
    }

    /**
     * Surely fetches nullable value from ResultSet.
     */
    private fun <A> nullable(v: A): A? {
        return if (underlying.wasNull()) null else v
    }

    fun statementOrNull(): Statement? {
        return nullable(underlying.statement)
    }

    fun warningsOrNull(): SQLWarning? {
        return underlying.warnings
    }

    fun next(): Boolean {
        return underlying.next()
    }

    fun close() {
        return underlying.close()
    }

    fun string(columnIndex: Int): String {
        return stringOrNull(columnIndex)!!
    }

    fun stringOrNull(columnIndex: Int): String? {
        return nullable(underlying.getString(columnIndex))
    }

    fun string(columnLabel: String): String {
        return stringOrNull(columnLabel)!!
    }

    fun stringOrNull(columnLabel: String): String? {
        return nullable(underlying.getString(columnLabel))
    }

    fun any(columnIndex: Int): Any {
        return anyOrNull(columnIndex)!!
    }

    fun anyOrNull(columnIndex: Int): Any? {
        return nullable(underlying.getObject(columnIndex))
    }

    fun any(columnLabel: String): Any {
        return anyOrNull(columnLabel)!!
    }

    fun anyOrNull(columnLabel: String): Any? {
        return nullable(underlying.getObject(columnLabel))
    }

    fun long(columnIndex: Int): Long {
        return longOrNull(columnIndex)!!
    }

    fun longOrNull(columnIndex: Int): Long? {
        return nullable(underlying.getLong(columnIndex))
    }

    fun long(columnLabel: String): Long {
        return longOrNull(columnLabel)!!
    }

    fun longOrNull(columnLabel: String): Long? {
        return nullable(underlying.getLong(columnLabel))
    }

    fun bytes(columnIndex: Int): ByteArray {
        return bytesOrNull(columnIndex)!!
    }

    fun bytesOrNull(columnIndex: Int): ByteArray? {
        return nullable(underlying.getBytes(columnIndex))
    }

    fun bytes(columnLabel: String): ByteArray {
        return bytesOrNull(columnLabel)!!
    }

    fun bytesOrNull(columnLabel: String): ByteArray? {
        return nullable(underlying.getBytes(columnLabel))
    }

    fun float(columnIndex: Int): Float {
        return floatOrNull(columnIndex)!!
    }

    fun floatOrNull(columnIndex: Int): Float? {
        return nullable(underlying.getFloat(columnIndex))
    }

    fun float(columnLabel: String): Float {
        return floatOrNull(columnLabel)!!
    }

    fun floatOrNull(columnLabel: String): Float? {
        return nullable(underlying.getFloat(columnLabel))
    }

    fun short(columnIndex: Int): Short {
        return shortOrNull(columnIndex)!!
    }

    fun shortOrNull(columnIndex: Int): Short? {
        return nullable(underlying.getShort(columnIndex))
    }

    fun short(columnLabel: String): Short {
        return shortOrNull(columnLabel)!!
    }

    fun shortOrNull(columnLabel: String): Short? {
        return nullable(underlying.getShort(columnLabel))
    }

    fun double(columnIndex: Int): Double {
        return doubleOrNull(columnIndex)!!
    }

    fun doubleOrNull(columnIndex: Int): Double? {
        return nullable(underlying.getDouble(columnIndex))
    }

    fun double(columnLabel: String): Double {
        return doubleOrNull(columnLabel)!!
    }

    fun doubleOrNull(columnLabel: String): Double? {
        return nullable(underlying.getDouble(columnLabel))
    }

    fun int(columnIndex: Int): Int {
        return intOrNull(columnIndex)!!
    }

    fun intOrNull(columnIndex: Int): Int? {
        return nullable(underlying.getInt(columnIndex))
    }

    fun int(columnLabel: String): Int {
        return intOrNull(columnLabel)!!
    }

    fun intOrNull(columnLabel: String): Int? {
        return nullable(underlying.getInt(columnLabel))
    }

    fun jodaDateTime(columnIndex: Int): DateTime {
        return jodaDateTimeOrNull(columnIndex)!!
    }

    fun jodaDateTimeOrNull(columnIndex: Int): DateTime? {
        val timestamp = sqlTimestampOrNull(columnIndex)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp)
        }
    }

    fun jodaDateTime(columnLabel: String): DateTime {
        return jodaDateTimeOrNull(columnLabel)!!
    }

    fun jodaDateTimeOrNull(columnLabel: String): DateTime? {
        val timestamp = sqlTimestampOrNull(columnLabel)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp)
        }
    }

    fun jodaLocalDate(columnIndex: Int): org.joda.time.LocalDate {
        return jodaLocalDateOrNull(columnIndex)!!
    }

    fun jodaLocalDateOrNull(columnIndex: Int): org.joda.time.LocalDate? {
        val timestamp = sqlTimestampOrNull(columnIndex)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp).toLocalDate()
        }
    }

    fun jodaLocalDateTime(columnIndex: Int): org.joda.time.LocalDateTime {
        return jodaLocalDateTimeOrNull(columnIndex)!!
    }

    fun jodaLocalDateTimeOrNull(columnIndex: Int): org.joda.time.LocalDateTime? {
        val timestamp = sqlTimestampOrNull(columnIndex)
        if (timestamp == null) {
            return null
        } else {
            return org.joda.time.LocalDateTime(timestamp)
        }
    }

    fun jodaLocalDateTime(columnLabel: String): org.joda.time.LocalDateTime {
        return jodaLocalDateTimeOrNull(columnLabel)!!
    }

    fun jodaLocalDateTimeOrNull(columnLabel: String): org.joda.time.LocalDateTime? {
        val timestamp = sqlTimestampOrNull(columnLabel)
        if (timestamp == null) {
            return null
        } else {
            return org.joda.time.LocalDateTime(timestamp)
        }
    }

    fun jodaLocalDate(columnLabel: String): org.joda.time.LocalDate {
        return jodaLocalDateOrNull(columnLabel)!!
    }

    fun jodaLocalDateOrNull(columnLabel: String): org.joda.time.LocalDate? {
        val timestamp = sqlTimestampOrNull(columnLabel)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp).toLocalDate()
        }
    }

    fun jodaLocalTime(columnIndex: Int): org.joda.time.LocalTime {
        return jodaLocalTimeOrNull(columnIndex)!!
    }

    fun jodaLocalTimeOrNull(columnIndex: Int): org.joda.time.LocalTime? {
        val timestamp = sqlTimestampOrNull(columnIndex)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp).toLocalTime()
        }
    }

    fun jodaLocalTime(columnLabel: String): org.joda.time.LocalTime {
        return jodaLocalTimeOrNull(columnLabel)!!
    }

    fun jodaLocalTimeOrNull(columnLabel: String): org.joda.time.LocalTime? {
        val timestamp = sqlTimestampOrNull(columnLabel)
        if (timestamp == null) {
            return null
        } else {
            return DateTime(timestamp).toLocalTime()
        }
    }

    fun zonedDateTime(columnIndex: Int): ZonedDateTime {
        return zonedDateTimeOrNull(columnIndex)!!
    }

    fun zonedDateTimeOrNull(columnIndex: Int): ZonedDateTime? = nullable(
        sqlTimestampOrNull(columnIndex)?.toInstant()?.let {
            ZonedDateTime.ofInstant(it, ZoneId.systemDefault())
        }
    )

    fun zonedDateTime(columnLabel: String): ZonedDateTime {
        return zonedDateTimeOrNull(columnLabel)!!
    }

    fun zonedDateTimeOrNull(columnLabel: String): ZonedDateTime? = nullable(
        sqlTimestampOrNull(columnLabel)?.toInstant()?.let {
            ZonedDateTime.ofInstant(it, ZoneId.systemDefault())
        }
    )

    fun offsetDateTime(columnIndex: Int): OffsetDateTime {
        return offsetDateTimeOrNull(columnIndex)!!
    }

    fun offsetDateTimeOrNull(columnIndex: Int): OffsetDateTime? = nullable(
        sqlTimestampOrNull(columnIndex)?.toInstant()?.let {
            OffsetDateTime.ofInstant(it, ZoneId.systemDefault())
        }
    )

    fun offsetDateTime(columnLabel: String): OffsetDateTime {
        return offsetDateTimeOrNull(columnLabel)!!
    }

    fun offsetDateTimeOrNull(columnLabel: String): OffsetDateTime? = nullable(
        sqlTimestampOrNull(columnLabel)?.toInstant()?.let {
            OffsetDateTime.ofInstant(it, ZoneId.systemDefault())
        }
    )

    fun instant(columnIndex: Int): Instant {
        return instantOrNull(columnIndex)!!
    }

    fun instantOrNull(columnIndex: Int): Instant? {
        return nullable(sqlTimestampOrNull(columnIndex)?.toInstant())
    }

    fun instant(columnLabel: String): Instant {
        return instantOrNull(columnLabel)!!
    }

    fun instantOrNull(columnLabel: String): Instant? {
        return nullable(sqlTimestampOrNull(columnLabel)?.toInstant())
    }

    fun localDateTime(columnIndex: Int): LocalDateTime {
        return localDateTimeOrNull(columnIndex)!!
    }

    fun localDateTimeOrNull(columnIndex: Int): LocalDateTime? {
        return sqlTimestampOrNull(columnIndex)?.toLocalDateTime()
    }

    fun localDateTime(columnLabel: String): LocalDateTime {
        return localDateTimeOrNull(columnLabel)!!
    }

    fun localDateTimeOrNull(columnLabel: String): LocalDateTime? {
        return sqlTimestampOrNull(columnLabel)?.toLocalDateTime()
    }

    fun localDate(columnIndex: Int): LocalDate {
        return localDateOrNull(columnIndex)!!
    }

    fun localDateOrNull(columnIndex: Int): LocalDate? {
        return sqlTimestampOrNull(columnIndex)?.toLocalDateTime()?.toLocalDate()
    }

    fun localDate(columnLabel: String): LocalDate {
        return localDateOrNull(columnLabel)!!
    }

    fun localDateOrNull(columnLabel: String): LocalDate? {
        return sqlTimestampOrNull(columnLabel)?.toLocalDateTime()?.toLocalDate()
    }

    fun localTime(columnIndex: Int): LocalTime {
        return localTimeOrNull(columnIndex)!!
    }

    fun localTimeOrNull(columnIndex: Int): LocalTime? {
        return sqlTimestampOrNull(columnIndex)?.toLocalDateTime()?.toLocalTime()
    }

    fun localTime(columnLabel: String): LocalTime {
        return localTimeOrNull(columnLabel)!!
    }

    fun localTimeOrNull(columnLabel: String): LocalTime? {
        return sqlTimestampOrNull(columnLabel)?.toLocalDateTime()?.toLocalTime()
    }

    fun sqlDate(columnIndex: Int): java.sql.Date {
        return sqlDateOrNull(columnIndex)!!
    }

    fun sqlDateOrNull(columnIndex: Int): java.sql.Date? {
        return nullable(underlying.getDate(columnIndex))
    }

    fun sqlDate(columnLabel: String): java.sql.Date {
        return sqlDateOrNull(columnLabel)!!
    }

    fun sqlDateOrNull(columnLabel: String): java.sql.Date? {
        return nullable(underlying.getDate(columnLabel))
    }

    fun sqlDate(columnIndex: Int, cal: java.util.Calendar): java.sql.Date {
        return sqlDateOrNull(columnIndex, cal)!!
    }

    fun sqlDateOrNull(columnIndex: Int, cal: java.util.Calendar): java.sql.Date? {
        return nullable(underlying.getDate(columnIndex, cal))
    }

    fun sqlDate(columnLabel: String, cal: java.util.Calendar): java.sql.Date {
        return sqlDateOrNull(columnLabel, cal)!!
    }

    fun sqlDateOrNull(columnLabel: String, cal: java.util.Calendar): java.sql.Date? {
        return nullable(underlying.getDate(columnLabel, cal))
    }

    fun boolean(columnIndex: Int): Boolean {
        return underlying.getBoolean(columnIndex)
    }

    fun boolean(columnLabel: String): Boolean {
        return underlying.getBoolean(columnLabel)
    }

    fun bigDecimal(columnIndex: Int): BigDecimal {
        return bigDecimalOrNull(columnIndex)!!
    }

    fun bigDecimalOrNull(columnIndex: Int): BigDecimal? {
        return nullable(underlying.getBigDecimal(columnIndex))
    }

    fun bigDecimal(columnLabel: String): BigDecimal {
        return bigDecimalOrNull(columnLabel)!!
    }

    fun bigDecimalOrNull(columnLabel: String): BigDecimal? {
        return nullable(underlying.getBigDecimal(columnLabel))
    }

    fun sqlTime(columnIndex: Int): java.sql.Time {
        return sqlTimeOrNull(columnIndex)!!
    }

    fun sqlTimeOrNull(columnIndex: Int): java.sql.Time? {
        return nullable(underlying.getTime(columnIndex))
    }

    fun sqlTime(columnLabel: String): java.sql.Time {
        return sqlTimeOrNull(columnLabel)!!
    }

    fun sqlTimeOrNull(columnLabel: String): java.sql.Time? {
        return nullable(underlying.getTime(columnLabel))
    }

    fun sqlTime(columnIndex: Int, cal: java.util.Calendar): java.sql.Time {
        return sqlTimeOrNull(columnIndex, cal)!!
    }

    fun sqlTimeOrNull(columnIndex: Int, cal: java.util.Calendar): java.sql.Time? {
        return nullable(underlying.getTime(columnIndex, cal))
    }

    fun sqlTime(columnLabel: String, cal: java.util.Calendar): java.sql.Time {
        return sqlTimeOrNull(columnLabel, cal)!!
    }

    fun sqlTimeOrNull(columnLabel: String, cal: java.util.Calendar): java.sql.Time? {
        return nullable(underlying.getTime(columnLabel, cal))
    }

    fun url(columnIndex: Int): URL {
        return urlOrNull(columnIndex)!!
    }

    fun urlOrNull(columnIndex: Int): URL? {
        return nullable(underlying.getURL(columnIndex))
    }

    fun url(columnLabel: String): URL {
        return urlOrNull(columnLabel)!!
    }

    fun urlOrNull(columnLabel: String): URL? {
        return nullable(underlying.getURL(columnLabel))
    }

    fun blob(columnIndex: Int): Blob {
        return blobOrNull(columnIndex)!!
    }

    fun blobOrNull(columnIndex: Int): Blob? {
        return nullable(underlying.getBlob(columnIndex))
    }

    fun blob(columnLabel: String): Blob {
        return blobOrNull(columnLabel)!!
    }

    fun blobOrNull(columnLabel: String): Blob? {
        return nullable(underlying.getBlob(columnLabel))
    }

    fun byte(columnIndex: Int): Byte {
        return byteOrNull(columnIndex)!!
    }

    fun byteOrNull(columnIndex: Int): Byte? {
        return nullable(underlying.getByte(columnIndex))
    }

    fun byte(columnLabel: String): Byte {
        return byteOrNull(columnLabel)!!
    }

    fun byteOrNull(columnLabel: String): Byte? {
        return nullable(underlying.getByte(columnLabel))
    }

    fun clob(columnIndex: Int): java.sql.Clob {
        return clobOrNull(columnIndex)!!
    }

    fun clobOrNull(columnIndex: Int): java.sql.Clob? {
        return nullable(underlying.getClob(columnIndex))
    }

    fun clob(columnLabel: String): java.sql.Clob {
        return clobOrNull(columnLabel)!!
    }

    fun clobOrNull(columnLabel: String): java.sql.Clob? {
        return nullable(underlying.getClob(columnLabel))
    }

    fun nClob(columnIndex: Int): NClob {
        return nClobOrNull(columnIndex)!!
    }

    fun nClobOrNull(columnIndex: Int): NClob? {
        return nullable(underlying.getNClob(columnIndex))
    }

    fun nClob(columnLabel: String): NClob {
        return nClobOrNull(columnLabel)!!
    }

    fun nClobOrNull(columnLabel: String): NClob? {
        return nullable(underlying.getNClob(columnLabel))
    }

    inline fun <reified T> array(columnIndex: Int): Array<T> = arrayOrNull(columnIndex)!!

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> arrayOrNull(columnIndex: Int): Array<T>? {
        val result = sqlArrayOrNull(columnIndex)?.array as Array<*>?
        return result?.map { it as T }?.toTypedArray()
    }

    inline fun <reified T> array(columnLabel: String): Array<T> = arrayOrNull(columnLabel)!!

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> arrayOrNull(columnLabel: String): Array<T>? {
        val result = sqlArrayOrNull(columnLabel)?.array as Array<Any>?
        return result?.map { it as T }?.toTypedArray()
    }

    fun sqlArray(columnIndex: Int): java.sql.Array {
        return sqlArrayOrNull(columnIndex)!!
    }

    fun sqlArrayOrNull(columnIndex: Int): java.sql.Array? {
        return nullable(underlying.getArray(columnIndex))
    }

    fun sqlArray(columnLabel: String): java.sql.Array {
        return sqlArrayOrNull(columnLabel)!!
    }

    fun sqlArrayOrNull(columnLabel: String): java.sql.Array? {
        return nullable(underlying.getArray(columnLabel))
    }

    fun asciiStream(columnIndex: Int): InputStream {
        return asciiStreamOrNull(columnIndex)!!
    }

    fun asciiStreamOrNull(columnIndex: Int): InputStream? {
        return nullable(underlying.getAsciiStream(columnIndex))
    }

    fun asciiStream(columnLabel: String): InputStream {
        return asciiStreamOrNull(columnLabel)!!
    }

    fun asciiStreamOrNull(columnLabel: String): InputStream? {
        return nullable(underlying.getAsciiStream(columnLabel))
    }

    fun sqlTimestamp(columnIndex: Int): java.sql.Timestamp {
        return sqlTimestampOrNull(columnIndex)!!
    }

    fun sqlTimestampOrNull(columnIndex: Int): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnIndex))
    }

    fun sqlTimestamp(columnLabel: String): java.sql.Timestamp {
        return sqlTimestampOrNull(columnLabel)!!
    }

    fun sqlTimestampOrNull(columnLabel: String): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnLabel))
    }

    fun sqlTimestamp(columnIndex: Int, cal: java.util.Calendar): java.sql.Timestamp {
        return sqlTimestampOrNull(columnIndex, cal)!!
    }

    fun sqlTimestampOrNull(columnIndex: Int, cal: java.util.Calendar): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnIndex, cal))
    }

    fun sqlTimestamp(columnLabel: String, cal: java.util.Calendar): java.sql.Timestamp {
        return sqlTimestampOrNull(columnLabel, cal)!!
    }

    fun sqlTimestampOrNull(columnLabel: String, cal: java.util.Calendar): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnLabel, cal))
    }

    fun uuid(columnLabel: String): java.util.UUID {
        return uuidOrNull(columnLabel)!!
    }

    fun uuidOrNull(columnLabel: String): java.util.UUID? {
        return nullable(underlying.getString(columnLabel))?.let {
            java.util.UUID.fromString(it)
        }
    }

    fun ref(columnIndex: Int): Ref {
        return refOrNull(columnIndex)!!
    }

    fun refOrNull(columnIndex: Int): Ref? {
        return nullable(underlying.getRef(columnIndex))
    }

    fun ref(columnLabel: String): Ref {
        return refOrNull(columnLabel)!!
    }

    fun refOrNull(columnLabel: String): Ref? {
        return nullable(underlying.getRef(columnLabel))
    }

    fun nCharacterStream(columnIndex: Int): Reader {
        return nCharacterStreamOrNull(columnIndex)!!
    }

    fun nCharacterStreamOrNull(columnIndex: Int): Reader? {
        return nullable(underlying.getNCharacterStream(columnIndex))
    }

    fun nCharacterStream(columnLabel: String): Reader {
        return nCharacterStreamOrNull(columnLabel)!!
    }

    fun nCharacterStreamOrNull(columnLabel: String): Reader? {
        return nullable(underlying.getNCharacterStream(columnLabel))
    }

    fun metaDataOrNull(): ResultSetMetaData {
        return underlying.metaData
    }

    fun binaryStream(columnIndex: Int): InputStream {
        return binaryStreamOrNull(columnIndex)!!
    }

    fun binaryStreamOrNull(columnIndex: Int): InputStream? {
        return nullable(underlying.getBinaryStream(columnIndex))
    }

    fun binaryStream(columnLabel: String): InputStream {
        return binaryStreamOrNull(columnLabel)!!
    }

    fun binaryStreamOrNull(columnLabel: String): InputStream? {
        return nullable(underlying.getBinaryStream(columnLabel))
    }

}
