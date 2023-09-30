package de.todesstoss.tsreports.listener

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener : Listener {

    private val plugin = TSReports.instance

    @EventHandler(
        priority = EventPriority.LOW
    )
    fun onJoin(
        event: PlayerJoinEvent
    ) {
        val conn = event.player
        if ( !conn.isOnline ) return
        val player = OfflinePlayer( conn )

        // if player joins for the first time save
        if ( !plugin.playerCache.has( player.uniqueId ) )
        {
            plugin.debug("${player.username} first join since database establishment.")
            plugin.playerCache.add( conn )
        }
        else
        {
            plugin.sqlManager.updateName( player )
            plugin.sqlManager.updateAddress( player )
        }

        serverWealthMessage( conn, player )
        reportWarningMessage( conn )

        if ( plugin.bungeeCord.updateList.contains( conn.uniqueId ) )
            plugin.bungeeCord.sendUpdate( conn )
    }

    @EventHandler(
        priority = EventPriority.LOW
    )
    fun onQuit(
        event: PlayerQuitEvent
    ) {
        val player = plugin.playerCache.get( event.player.uniqueId )
            ?: return

        if ( !PlayerUtils.hasPermission(event.player, "tsreports.autologin") && player.loggedIn )
        {
            player.loggedIn = false
            plugin.sqlManager.updateLoggedIn( player )
        }
    }

    private fun serverWealthMessage(
        conn: Player,
        player: OfflinePlayer
    ) {
        // (only with permission: tsreports.staff)
        if ( !PlayerUtils.hasPermission(conn, listOf("tsreports.staff", "tsreports.admin")) ) return

        val reports = plugin.reportCache.size()

        val loggedIn = when {
            player.loggedIn -> MessageBuilder("staff.loggedIn").get( conn.uniqueId ).toLegacyText()
            else -> MessageBuilder("staff.loggedOut").get( conn.uniqueId ).toLegacyText()
        }

        MessageBuilder("staff.join")
            .placeholders { it.replace("%loggedIn%", loggedIn)
                .replace("%reports%", "$reports") }
            .send( conn.uniqueId )
    }

    private fun reportWarningMessage(
        conn: Player
    ) {
        // (only with permission: tsreports.warnings)
        if ( !PlayerUtils.hasPermission(conn, listOf("tsreports.warnings", "tsreports.admin")) ) return

        val config = plugin.config
        val reports = plugin.reportCache.size()

        val medium = config.getInt("warnings.medium", 25)
        val high = config.getInt("warnings.high", 60)

        val message = when {
            reports >= high -> MessageBuilder("staff.highVulnerability").get( conn.uniqueId )
            reports >= medium -> MessageBuilder("staff.mediumVulnerability").get( conn.uniqueId )
            else -> MessageBuilder("staff.noVulnerability").get( conn.uniqueId )
        }

        conn.spigot().sendMessage( message )
    }

}