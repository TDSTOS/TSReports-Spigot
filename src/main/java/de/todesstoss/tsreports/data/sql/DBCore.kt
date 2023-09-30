package de.todesstoss.tsreports.data.sql

import com.zaxxer.hikari.HikariDataSource

internal interface DBCore {

    val dataSource: HikariDataSource

    fun execute(query: String)

    fun executeAsync(query: String)

    fun update(query: String)

    fun updateAsync(query: String)

    fun existsTable(table: String): Boolean

    fun existsColumn(table: String, column: String): Boolean

    fun close(): Boolean
    {
        dataSource.close()
        return dataSource.isClosed
    }

}