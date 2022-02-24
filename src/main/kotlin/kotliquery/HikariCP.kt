package kotliquery

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object HikariCP {

    private val pools: ConcurrentMap<String, HikariDataSource> = ConcurrentHashMap()

    fun default(url: String, username: String, password: String): HikariDataSource {
        return init("default", url, username, password)
    }

    fun init(
        name: String = "default",
        url: String,
        username: String,
        password: String,
        configPropertiesFn: HikariConfig.() -> Unit = { }
    ): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.username = username
        config.password = password
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        config.configPropertiesFn()

        val existing: HikariDataSource? = pools[name]
        if (existing != null && !existing.isClosed) {
            existing.close()
        }
        val ds = HikariDataSource(config)
        pools.put(name, ds)
        return ds
    }

    fun dataSource(name: String = "default"): HikariDataSource {
        val ds: HikariDataSource? = pools[name]
        if (ds != null && !ds.isClosed) {
            return ds
        } else {
            throw IllegalStateException("DataSource ($name) is absent.")
        }
    }

}