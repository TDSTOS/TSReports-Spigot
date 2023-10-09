package de.todesstoss.tsreports.command

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.inventories.language.LanguageSelectUI
import de.todesstoss.tsreports.util.Utils
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.asOfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import kotlin.streams.toList

class LanguageCommand : CommandExecutor, TabCompleter {

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
            InventoryDrawer.open( LanguageSelectUI(null, sender) )
            return true
        }

        val languages = plugin.configManager.getAvailableLocales()
            .stream().map { it.toString() }.toList()

        if ( !languages.contains( args[0] ) )
        {
            MessageBuilder("commands.language.usage")
                .send( sender.uniqueId )
            return true
        }

        val player = sender.asOfflinePlayer()
        player.language = Utils.stringToLocale( args[0] )
        plugin.sqlManager.updateLanguage( player )

        MessageBuilder("commands.language.selected")
            .placeholders { it.replace("%language%", args[0]) }
            .send( sender.uniqueId )
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>
    {
        return plugin.configManager.getAvailableLocales().stream()
            .map { it.toString() }.toList().toMutableList()
    }

}