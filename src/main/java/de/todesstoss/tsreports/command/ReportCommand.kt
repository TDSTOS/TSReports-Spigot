package de.todesstoss.tsreports.command

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.inventories.report.ReportPlayerUI
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.asOfflinePlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import kotlin.streams.toList

class ReportCommand : CommandExecutor, TabCompleter {

    private val plugin = TSReports.instance

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean
    {
        if ( sender !is Player )
        {
            MessageBuilder("onlyPlayer")
                .send( null )
            return true
        }

        if ( args.isEmpty() )
        {
            InventoryDrawer.open( ReportPlayerUI(null, sender, null) )
            return true
        }

        val target = plugin.playerCache.get( args[0] )

        if ( target == null )
        {
            MessageBuilder("playerNotFound")
                .send( sender.uniqueId )
            return true
        }

        InventoryDrawer.open( ReportPlayerUI(null, sender, target.uniqueId) )
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>
    {
        if ( sender !is Player ) return mutableListOf()

        if ( !plugin.isBungee )
            return Bukkit.getOnlinePlayers().stream()
                .filter { sender.asOfflinePlayer().isAdmin }
                .map { it.name }
                .toList()
                .toMutableList()

        return plugin.bungeeCord.playerList( sender ).stream()
            .map { plugin.playerCache.get( it ) }
            .filter { Objects.nonNull( it ) }
            .filter { it!!.isAdmin }
            .map { it!!.username }
            .toList()
            .toMutableList()
    }

}
