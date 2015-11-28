package kotliquery

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Date
import java.util.*

data class Row(
        val underlying: ResultSet,
        val cursor: Int = 0) : Sequence<Row> {

    private fun nullable<A>(v: A): A? {
        return if (underlying.wasNull()) null else v
    }

    fun nClob(columnIndex: Int): NClob? {
        return nullable(underlying.getNClob(columnIndex))
    }

    fun nClob(columnLabel: String): NClob? {
        return nullable(underlying.getNClob(columnLabel))
    }

    fun statement(): Statement? {
        return nullable(underlying.statement)
    }

    fun sqlDate(columnIndex: Int): java.sql.Date? {
        return nullable(underlying.getDate(columnIndex))
    }

    fun sqlDate(columnLabel: String): java.sql.Date? {
        return nullable(underlying.getDate(columnLabel))
    }

    fun sqlDate(columnIndex: Int, cal: Calendar): Date? {
        return nullable(underlying.getDate(columnIndex, cal))
    }

    fun sqlDate(columnLabel: String, cal: Calendar): Date? {
        return nullable(underlying.getDate(columnLabel, cal))
    }

    fun warnings(): SQLWarning? {
        return underlying.warnings
    }

    fun close(): Unit {
        return underlying.close()
    }

    fun boolean(columnIndex: Int): Boolean {
        return underlying.getBoolean(columnIndex)
    }

    fun boolean(columnLabel: String): Boolean {
        return underlying.getBoolean(columnLabel)
    }

    fun bigDecimal(columnIndex: Int): BigDecimal? {
        return nullable(underlying.getBigDecimal(columnIndex))
    }

    fun bigDecimal(columnLabel: String): BigDecimal? {
        return nullable(underlying.getBigDecimal(columnLabel))
    }

    fun sqlTime(columnIndex: Int): java.sql.Time? {
        return nullable(underlying.getTime(columnIndex))
    }

    fun sqlTime(columnLabel: String): java.sql.Time? {
        return nullable(underlying.getTime(columnLabel))
    }

    fun sqlTime(columnIndex: Int, cal: Calendar?): java.sql.Time? {
        return nullable(underlying.getTime(columnIndex, cal))
    }

    fun sqlTime(columnLabel: String, cal: Calendar?): java.sql.Time? {
        return nullable(underlying.getTime(columnLabel, cal))
    }

    fun next(): Boolean {
        return underlying.next()
    }

    fun float(columnIndex: Int): Float? {
        return nullable(underlying.getFloat(columnIndex))
    }

    fun float(columnLabel: String): Float? {
        return nullable(underlying.getFloat(columnLabel))
    }

    fun url(columnIndex: Int): URL? {
        return nullable(underlying.getURL(columnIndex))
    }

    fun url(columnLabel: String): URL? {
        return nullable(underlying.getURL(columnLabel))
    }

    fun blob(columnIndex: Int): Blob? {
        return nullable(underlying.getBlob(columnIndex))
    }

    fun blob(columnLabel: String): Blob? {
        return nullable(underlying.getBlob(columnLabel))
    }

    fun byte(columnIndex: Int): Byte? {
        return nullable(underlying.getByte(columnIndex))
    }

    fun byte(columnLabel: String): Byte? {
        return nullable(underlying.getByte(columnLabel))
    }

    fun string(columnIndex: Int): String? {
        return nullable(underlying.getString(columnIndex))
    }

    fun string(columnLabel: String): String? {
        return nullable(underlying.getString(columnLabel))
    }

    fun any(columnIndex: Int): Any? {
        return nullable(underlying.getObject(columnIndex))
    }

    fun any(columnLabel: String): Any? {
        return nullable(underlying.getObject(columnLabel))
    }

    fun long(columnIndex: Int): Long? {
        return nullable(underlying.getLong(columnIndex))
    }

    fun long(columnLabel: String): Long? {
        return nullable(underlying.getLong(columnLabel))
    }

    fun clob(columnIndex: Int): java.sql.Clob? {
        return nullable(underlying.getClob(columnIndex))
    }

    fun clob(columnLabel: String): java.sql.Clob? {
        return nullable(underlying.getClob(columnLabel))
    }

    fun sqlArray(columnIndex: Int): java.sql.Array? {
        return nullable(underlying.getArray(columnIndex))
    }

    fun sqlArray(columnLabel: String): java.sql.Array? {
        return nullable(underlying.getArray(columnLabel))
    }

    fun short(columnIndex: Int): Short? {
        return nullable(underlying.getShort(columnIndex))
    }

    fun short(columnLabel: String): Short? {
        return nullable(underlying.getShort(columnLabel))
    }

    fun asciiStream(columnIndex: Int): InputStream? {
        return nullable(underlying.getAsciiStream(columnIndex))
    }

    fun asciiStream(columnLabel: String): InputStream? {
        return nullable(underlying.getAsciiStream(columnLabel))
    }

    fun sqlTimestamp(columnIndex: Int): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnIndex))
    }

    fun sqlTimestamp(columnLabel: String): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnLabel))
    }

    fun sqlTimestamp(columnIndex: Int, cal: Calendar): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnIndex, cal))
    }

    fun sqlTimestamp(columnLabel: String, cal: Calendar): java.sql.Timestamp? {
        return nullable(underlying.getTimestamp(columnLabel, cal))
    }

    fun ref(columnIndex: Int): Ref? {
        return nullable(underlying.getRef(columnIndex))
    }

    fun ref(columnLabel: String): Ref? {
        return nullable(underlying.getRef(columnLabel))
    }

    fun nCharacterStream(columnIndex: Int): Reader? {
        return nullable(underlying.getNCharacterStream(columnIndex))
    }

    fun nCharacterStream(columnLabel: String): Reader? {
        return nullable(underlying.getNCharacterStream(columnLabel))
    }

    fun bytes(columnIndex: Int): ByteArray? {
        return nullable(underlying.getBytes(columnIndex))
    }

    fun bytes(columnLabel: String): ByteArray? {
        return nullable(underlying.getBytes(columnLabel))
    }

    fun double(columnIndex: Int): Double? {
        return nullable(underlying.getDouble(columnIndex))
    }

    fun double(columnLabel: String): Double? {
        return nullable(underlying.getDouble(columnLabel))
    }

    fun int(columnIndex: Int): Int? {
        return nullable(underlying.getInt(columnIndex))
    }

    fun int(columnLabel: String): Int? {
        return nullable(underlying.getInt(columnLabel))
    }

    fun metaData(): ResultSetMetaData {
        return underlying.metaData
    }

    fun binaryStream(columnIndex: Int): InputStream? {
        return nullable(underlying.getBinaryStream(columnIndex))
    }

    fun binaryStream(columnLabel: String): InputStream? {
        return nullable(underlying.getBinaryStream(columnLabel))
    }

    class RowIterator(val rs: ResultSet, val position: Int) : Iterator<Row> {
        override fun next(): Row {
            return Row(rs, position + 1)
        }

        override fun hasNext(): Boolean {
            return rs.isClosed == false && rs.next();
        }
    }

    override fun iterator(): Iterator<Row> {
        return RowIterator(underlying, cursor)
    }

}