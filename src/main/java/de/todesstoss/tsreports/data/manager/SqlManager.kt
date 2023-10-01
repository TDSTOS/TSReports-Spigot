package de.todesstoss.tsreports.data.manager

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.data.`object`.ReportStatus
import de.todesstoss.tsreports.data.sql.DBCore
import de.todesstoss.tsreports.data.sql.H2Core
import de.todesstoss.tsreports.data.sql.MySQLCore
import de.todesstoss.tsreports.util.Utils
import java.sql.SQLException
import java.util.*

class SqlManager {

    private val plugin = TSReports.instance
    private lateinit var core: DBCore

    init {
        initializeTables()
    }

    private fun initializeTables()
    {
        val config = plugin.config

        plugin.debug("Establishing connection to database.")

        core = if ( provider() == "MySQL" ) {
            MySQLCore(
                config.getString("mysql.host")!!,
                config.getString("mysql.database")!!,
                config.getInt("mysql.port"),
                config.getString("mysql.username")!!,
                config.getString("mysql.password")!!)
        } else H2Core()

        plugin.debug("Checking if table 'tsreports_players' exists.")

        if ( !core.existsTable("tsreports_players") )
        {
            plugin.logger.info("Creating table: tsreports_players")

            val query = "CREATE TABLE IF NOT EXISTS tsreports_players (" +
                    if ( provider() == "MySQL" ) " uuid VARCHAR(72) NOT NULL,"
                    else " uuid VARCHAR(72) NOT NULL PRIMARY KEY," +
                            " username VARCHAR(16)," +
                            " address VARCHAR(35)," +
                            " language VARCHAR(10)," +
                            " loggedIn VARCHAR(1)" +
                            if ( provider() == "MySQL" ) ", PRIMARY KEY (uuid))"
                            else ")"

            core.executeAsync(query)
        }

        plugin.debug("Checking if table 'tsreports_reports' exists.")

        if ( !core.existsTable("tsreports_reports") )
        {
            plugin.logger.info("Creating table: tsreports_reports")

            val query = "CREATE TABLE IF NOT EXISTS tsreports_reports (" +
                    if ( provider() == "MySQL" ) " id BIGINT NOT NULL,"
                    else " id BIGINT NOT NULL PRIMARY KEY," +
                            " username VARCHAR(16)," +
                            " uniqueId VARCHAR(72)," +
                            " address VARCHAR(35)" +
                            " reason VARCHAR," +
                            " operator VARCHAR(72)," +
                            " server VARCHAR," +
                            " status VARCHAR(20)," +
                            " processing VARCHAR(72)," +
                            " timeOfReport LONGTEXT" +
                            if ( provider() == "MySQL" ) ", PRIMARY KEY (id))"
                            else ")"

            core.executeAsync( query )
        }
    }

    fun provider(): String
    {
        plugin.debug("Checking database name via provider method.")
        return if ( plugin.config.getBoolean("mysql.enabled") ) "MySQL" else "H2"
    }

    fun reports(): Map<Int, ReportFile>
    {
        plugin.debug("Selecting all reports from database.")
        val query = "SELECT * FROM tsreports_reports"
        val reports = hashMapOf<Int, ReportFile>()

        try {
            core.dataSource.use {

                val result = it.connection.createStatement().executeQuery( query )
                while ( result.next() )
                {
                    val id = result.getInt("id")
                    val username = result.getString("username")
                    val uniqueId = UUID.fromString( result.getString("uniqueId") )
                    val address = result.getString("address")
                    val reason = result.getString("reason")
                    val operator = UUID.fromString( result.getString("operator") )
                    val server = result.getString("server")
                    val status = ReportStatus.valueOf( result.getString("status") )
                    val processing = if ( result.getString("processing") == null ) null
                    else UUID.fromString( result.getString("processing") )
                    val timeOfReport = result.getLong("timeOfReport")

                    reports[id] = ReportFile(id, username, uniqueId, address, reason, operator, server, status, processing, timeOfReport)
                }

            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        plugin.logger.info("Loaded ${reports.size} reports.")
        return reports
    }

    fun reports(
        uuid: UUID
    ): List<ReportFile>
    {
        plugin.debug("Selecting all reports from database with uniqueId $uuid")
        val query = "SELECT * FROM tsreports_reports WHERE uniqueId='$uuid'"
        val reports = arrayListOf<ReportFile>()

        try {
            core.dataSource.use {

                val result = it.connection.createStatement().executeQuery( query )
                while ( result.next() )
                {
                    val id = result.getInt("id")
                    val username = result.getString("username")
                    val address = result.getString("address")
                    val reason = result.getString("reason")
                    val operator = UUID.fromString( result.getString("operator") )
                    val server = result.getString("server")
                    val status = ReportStatus.valueOf( result.getString("status") )
                    val processing = if ( result.getString("processing") == null ) null
                    else UUID.fromString( result.getString("processing") )
                    val timeOfReport = result.getLong("timeOfReport")

                    reports.add( ReportFile(id, username, uuid, address, reason, operator, server, status, processing, timeOfReport) )
                }

            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        plugin.debug("Found ${reports.size} reports from uniqueId $uuid")
        return reports
    }

    fun addReport(
        report: ReportFile
    ) {
        plugin.debug("Adding report to database.")
        val query = "INSERT INTO tsreports_reports VALUES (" +
                "'${report.id}',"
        "'${report.username}'," +
                "'${report.uniqueId}'," +
                "'${report.address}'," +
                "'${report.reason}'," +
                "'${report.operator}'," +
                "'${report.server}'," +
                "'${report.status}'," +
                "'${report.processing}'," +
                "'${report.timeOfReport}')"
        core.updateAsync( query )
    }

    fun removeReport(
        id: Int
    ) {
        plugin.debug("Removing report from database.")
        val query = "DELETE FROM tsreports_reports WHERE id='$id'"
        core.updateAsync( query )
    }

    fun updateStatus(
        report: ReportFile
    ) {
        plugin.debug("Updating status in database.")
        val query = "UPDATE tsreports_reports SET status='${report.status}' WHERE id='${report.id}'"
        core.updateAsync( query )
    }

    fun updateProcessing(
        report: ReportFile
    ) {
        plugin.debug("Updating processing in database.")
        val query = "UPDATE tsreports_reports SET processing='${report.processing}' WHERE id='${report.id}'"
        core.updateAsync( query )
    }

    fun clearReports()
    {
        plugin.debug("Clearing reports out of database.")
        val query = "DELETE FROM tsreports_reports"
        core.updateAsync( query )
    }

    fun offlinePlayers(): Map<UUID, OfflinePlayer>
    {
        plugin.debug("Selecting all offlinePlayers from database.")
        val query = "SELECT * FROM tsreports_players"
        val offlinePlayers = hashMapOf<UUID, OfflinePlayer>()

        try {
            core.dataSource.use {

                val result = it.connection.createStatement().executeQuery( query )
                while ( result.next() )
                {
                    val uuid = UUID.fromString( result.getString("uuid") )
                    val username = result.getString("username")
                    val address = result.getString("address")
                    val language = Utils.stringToLocale( result.getString("language") )
                    val loggedIn = result.getBoolean("loggedIn")

                    offlinePlayers[uuid] = OfflinePlayer(uuid, username, address, language, loggedIn)
                }

            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        plugin.logger.info("Loaded ${offlinePlayers.size} offline players.")
        return offlinePlayers
    }

    fun addOfflinePlayer(
        offlinePlayer: OfflinePlayer
    ) {
        plugin.debug("Adding offlinePlayer to database.")
        val query = "INSERT INTO tsreports_players VALUES (" +
                "'${offlinePlayer.uniqueId}'," +
                "'${offlinePlayer.username}'," +
                "'${offlinePlayer.address}'," +
                "'${offlinePlayer.language}'," +
                "'${offlinePlayer.loggedIn.number()}')"
        core.updateAsync( query )
    }

    fun removeOfflinePlayer(
        uuid: UUID
    ) {
        plugin.debug("Removing offlinePlayer from database.")
        val query = "DELETE FROM tsreports_players WHERE uuid='$uuid'"
        core.updateAsync( query )
    }

    fun updateName(
        offlinePlayer: OfflinePlayer
    ) {
        plugin.debug("Updating name in database.")
        val query = "UPDATE tsreports_players SET username='${offlinePlayer.username}' WHERE uuid='${offlinePlayer.uniqueId}'"
        core.updateAsync( query )
    }

    fun updateAddress(
        offlinePlayer: OfflinePlayer
    ) {
        plugin.debug("Updating address in database.")
        val query = "UPDATE tsreports_players SET address='${offlinePlayer.address} WHERE uuid='${offlinePlayer.uniqueId}''"
        core.updateAsync( query )
    }

    fun updateLanguage(
        offlinePlayer: OfflinePlayer
    ) {
        plugin.debug("Updating language in database.")
        val query = "UPDATE tsreports_players SET language='${offlinePlayer.language}' WHERE uuid='${offlinePlayer.uniqueId}'"
        core.updateAsync( query )
    }

    fun updateLoggedIn(
        offlinePlayer: OfflinePlayer
    ) {
        plugin.debug("Updating loggedIn in database.")
        val query = "UPDATE tsreports_players SET loggedIn='${offlinePlayer.loggedIn.number()}' WHERE uuid='${offlinePlayer.uniqueId}'"
        core.updateAsync( query )
    }

    internal fun close()
    {
        plugin.debug("Closing connection to database.")
        val logger = plugin.logger
        var tries = 0

        logger.info("Trying to close database connection...")

        while ( !core.close() && tries < 3 )
        {
            logger.info("Failed to close database connection. Retrying... ($tries / 3)")
            tries++
        }

        if ( tries == 3 )
            logger.info("Failed to close database connection. Please resolve this problem.")
        else
            logger.info("Successfully closed database connection.")
    }

    private fun Boolean.number(): String
    {
        return if ( this ) "1" else "0"
    }

}