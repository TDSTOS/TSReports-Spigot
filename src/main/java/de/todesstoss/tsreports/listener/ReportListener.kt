package de.todesstoss.tsreports.listener

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.event.ReportEvent
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ReportListener : Listener {

    private val plugin = TSReports.instance

    @EventHandler(
        priority = EventPriority.LOW,
        ignoreCancelled = true
    )
    fun onReport(
        event: ReportEvent
    ) {
        val report = event.report

        val operator = report.operator
        val targetUuid = report.uniqueId

        if ( PlayerUtils.isOnline( targetUuid ) &&
            PlayerUtils.hasPermission( targetUuid, listOf("tsreports.bypass.report") ) )
        {
            MessageBuilder("report.bypassed")
                .placeholders { it.replace("%name%", report.username) }
                .send( operator )
            return
        }
        else if ( plugin.config.getStringList("bypass-reports-uuid-list").contains( targetUuid.toString() ) )
        {
            MessageBuilder("report.bypassed")
                .placeholders { it.replace("%name%", report.username) }
                .send( operator )
            return
        }

        plugin.reportCache.add( report )

        MessageBuilder("report.reported")
            .placeholders { it.replace("%target%", report.username) }
            .send( operator )

        Bukkit.getOnlinePlayers().stream()
            .filter { PlayerUtils.hasPermission(it, listOf("tsreports.notify")) }
            .forEach {
                MessageBuilder("report.notify")
                    .placeholders { s -> s.replace("%id%", "${report.id}") }
                    .send( it.uniqueId )
            }

        if ( plugin.isBungee )
            if (!PlayerUtils.isOnline( targetUuid )) plugin.bungeeCord.updateList.add( targetUuid )
            else plugin.bungeeCord.sendUpdate( Bukkit.getPlayer( targetUuid )!! )
    }

}