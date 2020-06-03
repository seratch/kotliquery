package kotliquery

import kotliquery.action.ResultQueryActionBuilder
import java.sql.Types

fun Query.mapper() : ResultQueryActionBuilder<MutableMap<Any, Any?>> {
    return ResultQueryActionBuilder(this, ::extractor)
}

fun extractor(row: Row): MutableMap<Any, Any?> {
    val map = mutableMapOf<Any, Any?>()
    val metaData = row.underlying.metaData
    for (index in 1..metaData.columnCount) {
        val columnName = metaData.getColumnLabel(index);
        when(metaData.getColumnType(index)) {
            Types.BIT -> map.put(columnName, row.int(index))
            Types.TINYINT -> map.put(columnName, row.int(index))
            Types.SMALLINT -> map.put(columnName, row.int(index))
            Types.INTEGER -> map.put(columnName, row.int(index))
            Types.BIGINT -> map.put(columnName, row.int(index))
            Types.FLOAT -> map.put(columnName, row.float(index))
//            Types.REAL -> map.put(columnName, row.string(index))
            Types.DOUBLE -> map.put(columnName, row.double(index))
            Types.NUMERIC -> map.put(columnName, row.int(index))
            Types.DECIMAL -> map.put(columnName, row.bigDecimal(index))
            Types.CHAR -> map.put(columnName, row.string(index))
            Types.VARCHAR -> map.put(columnName, row.string(index))
            Types.LONGVARCHAR -> map.put(columnName, row.string(index))
            Types.DATE -> map.put(columnName, row.jodaLocalDate(index))
            Types.TIME -> map.put(columnName, row.jodaLocalTime(index))
            Types.TIMESTAMP -> map.put(columnName, row.jodaDateTime(index))
            Types.BINARY -> map.put(columnName, row.binaryStream(index))
            Types.VARBINARY -> map.put(columnName, row.binaryStream(index))
            Types.LONGVARBINARY -> map.put(columnName, row.binaryStream(index))
            Types.NULL -> map.put(columnName, row.anyOrNull(index))
            Types.OTHER -> map.put(columnName, row.string(index))
//            Types.JAVA_OBJECT -> map.put(columnName, row.string(index))
//            Types.DISTINCT -> map.put(columnName, row.string(index))
//            Types.STRUCT -> map.put(columnName, row.string(index))
//            Types.ARRAY -> map.put(columnName, row.string(index))
            Types.BLOB -> map.put(columnName, row.binaryStream(index))
//            Types.CLOB -> map.put(columnName, row.string(index))
//            Types.REF -> map.put(columnName, row.string(index))
//            Types.DATALINK -> map.put(columnName, row.string(index))
            Types.BOOLEAN -> map.put(columnName, row.boolean(index))
//            Types.ROWID -> map.put(columnName, row.string(index))
        }
    }
    return map
}
