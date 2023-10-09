package de.todesstoss.tsreports.command

import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.inventories.StaffSelectUI
import de.todesstoss.tsreports.inventory.inventories.manage.ManageReportsUI
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import de.todesstoss.tsreports.util.player.asOfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AdminCommand : CommandExecutor, TabCompleter {

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

        if ( !PlayerUtils.hasPermission( sender, "tsreports.staff" ) )
        {
            MessageBuilder("noPermission")
                .send( sender.uniqueId )
            return true
        }

        if ( !sender.asOfflinePlayer().isAdmin )
        {
            InventoryDrawer.open( ManageReportsUI(null, sender) )
            return true
        }

        InventoryDrawer.open( StaffSelectUI(null, sender) )
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>
    {
        return mutableListOf()
    }

}