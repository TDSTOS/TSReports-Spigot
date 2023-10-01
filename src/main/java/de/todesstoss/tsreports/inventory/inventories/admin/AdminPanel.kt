package de.todesstoss.tsreports.inventory.inventories.admin

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.cache.PlayerCache
import de.todesstoss.tsreports.data.cache.ReportCache
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import kotlin.streams.toList

class AdminPanel(
    override val parent: UIFrame?,
    override val viewer: Player
) : UIFrame(parent, viewer) {

    private val plugin = TSReports.instance

    override val title: String
        get() = Messages.GUI_ADMINPANEL_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 5

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        addReload()
        addInfo()
        addClearDatabase()
    }

    private fun addReload()
    {
        val lore = Messages.GUI_ADMINPANEL_RELOAD_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.RED_STAINED_GLASS_PANE)
            .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString( viewer.uniqueId ))
            .lore( lore )
            .slot( 20 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            val start = System.currentTimeMillis()

            plugin.configManager.setup()
            plugin.playerCache = PlayerCache( plugin.sqlManager )
            plugin.reportCache = ReportCache( plugin.sqlManager )

            val finish = System.currentTimeMillis() - start
            plugin.lastReload = finish

            MessageBuilder("admin.reload")
                .placeholders { it.replace("%time%", "$finish") }
                .send( viewer.uniqueId )
        }
        comp.confirmationRequired(ClickType.LEFT, ClickType.RIGHT)
        add( comp )
    }

    private fun addInfo()
    {
        val lastReload = (System.currentTimeMillis() - plugin.lastReload).convert()
        val reports = plugin.reportCache.reports().size
        val staffOnline = Bukkit.getOnlinePlayers().stream()
            .filter { PlayerUtils.hasPermission(it, "tsreports.staff") && it.uniqueId != viewer.uniqueId }
            .count()

        val lore = Messages.GUI_ADMINPANEL_INFO_LORE.getList( viewer.uniqueId )
            .stream().map { it.replace("%lastReload%", lastReload).replace("%reports%", "$reports")
            .replace("%staffOnline%", "$staffOnline")}.toList()
        val comp = UIComponentImpl.Builder(Material.OAK_SIGN)
            .name(Messages.GUI_ADMINPANEL_INFO_NAME.getString( viewer.uniqueId ))
            .lore( lore )
            .slot( 22 )
            .build()
        add( comp )
    }

    private fun addClearDatabase()
    {
        val lore = Messages.GUI_ADMINPANEL_CLEARDATABASE_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.BLACK_STAINED_GLASS_PANE)
            .name(Messages.GUI_ADMINPANEL_CLEARDATABASE_NAME.getString( viewer.uniqueId ))
            .lore( lore )
            .slot( 24 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            val start = System.currentTimeMillis()

            plugin.sqlManager.clearReports()

            MessageBuilder("admin.cleared")
                .placeholders { it.replace("%time%", "${System.currentTimeMillis() - start}") }
                .send( viewer.uniqueId )
        }
        comp.confirmationRequired(ClickType.LEFT, ClickType.RIGHT)
        add( comp )
    }

}

private fun Long.convert(): String
{
    val seconds = this / 1000 % 60
    val minutes = this / (1000 * 60) % 60
    val hours = this / (1000 * 60 * 60) % 24
    val days = this / (1000 * 60 * 60 * 24)

    return if (days > 0) {
        "${days}d ${hours}h ${minutes}m ${seconds}s"
    } else if (hours > 0) {
        "${hours}h ${minutes}m ${seconds}s"
    } else if (minutes > 0) {
        "${minutes}m ${seconds}s"
    } else if (seconds > 0) {
        "${seconds}s"
    } else {
        "${this}ms"
    }
}
