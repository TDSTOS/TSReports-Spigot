package de.todesstoss.tsreports.inventory.inventories.report

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.event.ReportEvent
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.message.MessageBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*

class ReportPlayerUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    private var target: UUID?,
    private var currentReason: String = "N/A"
) : UIFrame(parent, viewer) {

    private val plugin = TSReports.instance

    override val title: String
        get() = Messages.GUI_REPORTPLAYER_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 5

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        addPlayer()
        addReason()
        addSend()
    }

    private fun addPlayer()
    {
        val name = if ( target == null ) "§cN/A" else
            plugin.playerCache.get( target!! )!!.username

        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_REPORTPLAYER_PLAYER_NAME.getString( viewer.uniqueId )
                .replace("%name%", name))
            .skull( if ( target != null ) name else "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgyYjc4ZGE2ZWU3MTNkNWFjZmU1ZmNiMDc1NGVlNTY5MDA4MzFhNTA5ODMxMzA2NDEwOGRlNmU3ZTQwNjgzOSJ9fX0=" )
            .slot( 20 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( PlayerSelectUI(this, viewer, currentReason) )
        }
        add( comp )
    }

    private fun addReason()
    {
        val lore = Messages.GUI_REPORTPLAYER_REASON_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_REPORTPLAYER_REASON_NAME.getString( viewer.uniqueId )
                .replace("%reason%", "§c$currentReason"))
            .skull( "" )
            .lore( lore )
            .slot( 22 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( ReasonSelectUI(this, viewer, target) )
        }
        add( comp )
    }

    private fun addSend()
    {
        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_REPORTPLAYER_CONFIRM_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMwZjQ1MzdkMjE0ZDM4NjY2ZTYzMDRlOWM4NTFjZDZmN2U0MWEwZWI3YzI1MDQ5YzlkMjJjOGM1ZjY1NDVkZiJ9fX0=")
            .slot( 24 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            if ( target == null )
            {
                MessageBuilder("report.noPlayerSelected")
                    .send( viewer.uniqueId )
                return@listener
            }

            if ( currentReason == "N/A" )
            {
                MessageBuilder("report.noReasonSelected")
                    .send( viewer.uniqueId )
                return@listener
            }

            val target = plugin.playerCache.get( target!! )!!
            val report = ReportFile( target, viewer.uniqueId, currentReason )

            plugin.server.pluginManager.callEvent( ReportEvent(report, false) )
        }
        comp.confirmationRequired( ClickType.LEFT, ClickType.RIGHT )
        add( comp )
    }

}
