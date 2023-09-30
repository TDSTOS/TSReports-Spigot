package de.todesstoss.tsreports.data.sql

import com.zaxxer.hikari.HikariDataSource
import de.todesstoss.tsreports.TSReports
import java.sql.SQLException

class MySQLCore(
    host: String,
    database: String,
    port: Int,
    username: String,
    password: String
) : DBCore {

    private val plugin = TSReports.instance
    override val dataSource = HikariDataSource()

    init {
        val name = plugin.description.name
        dataSource.poolName = "[$name] Hikari"
        plugin.logger.info("Loading storage provider: MySQL")
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        dataSource.jdbcUrl = "jdbc:mysql://$host:$port/$database?useSSL=false&characterEncoding=utf-8"
        dataSource.username = username
        dataSource.password = password
    }

    override fun execute(
        query: String
    ) {
        try {

            dataSource.connection.use { it.createStatement().execute(query) }

        } catch (e: SQLException) {

            plugin.logger.severe("Error: ${e.message}")
            plugin.logger.severe("Query: $query")

        }
    }

    override fun executeAsync(
        query: String
    ) {
        plugin.scheduler.runTaskAsynchronously(plugin) { -> execute( query ) }
    }

    override fun update(
        query: String
    ) {
        try {

            dataSource.connection.use { it.createStatement().executeUpdate(query) }

        } catch (e: SQLException) {

            plugin.logger.severe("Error: ${e.message}")
            plugin.logger.severe("Query: $query")

        }
    }

    override fun updateAsync(
        query: String
    ) {
        plugin.scheduler.runTaskAsynchronously(plugin) { -> update( query ) }
    }

    override fun existsTable(
        table: String
    ): Boolean
    {
        return try {

            dataSource.connection.use { it.metaData.getTables(null, null, table, null).next() }

        } catch (e: SQLException) {

            plugin.logger.severe("Failed to check if table $table exists: ${e.message}")
            false

        }
    }

    override fun existsColumn(
        table: String,
        column: String
    ): Boolean
    {
        return try {

            dataSource.connection.use { it.metaData.getColumns(null, null, table, column).next() }

        } catch (e: SQLException) {

            plugin.logger.severe("Failed to check if column $column exists in table $table: ${e.message}")
            false

        }
    }

}