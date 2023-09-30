package de.todesstoss.tsreports.inventory.inventories.report

import de.todesstoss.tsreports.inventory.Components
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.inventory.Paginator
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import kotlin.streams.toList

class PlayerSelectUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    private val currentReason: String = ""
) : UIFrame(parent, viewer) {

    private val players = Bukkit.getOnlinePlayers().stream()
        .filter { !PlayerUtils.hasPermission(it, listOf("tsreports.bypass.report", "tsreports.admin")) }
        .toList()

    private val paginator = Paginator( size - 9, players )

    override val title: String
        get() = Messages.GUI_PLAYERSELECT_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 6

    override val border: Boolean
        get() = false

    override fun createComponents()
    {
        add( Components.back(parent, 49, viewer) )

        var slot = 0
        var index = paginator.minIndex

        while ( paginator.isValidIndex( index ) )
        {
            val player = players[index]
            addPlayer( slot, player )

            slot++
            index++
        }

        val previous = Components.previousPage(47, this::previousPage, paginator, viewer)
        if ( previous != null ) add( previous )

        val next = Components.nextPage(51, this::nextPage, paginator, viewer)
        if ( next != null ) add( next )
    }

    private fun addPlayer(
        slot: Int,
        player: Player
    ) {
        val lore = Messages.GUI_PLAYERSELECT_PLAYER_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_PLAYERSELECT_PLAYER_NAME.getString( viewer.uniqueId )
                .replace("%name%", player.displayName))
            .skull( player.name )
            .lore( lore )
            .slot( slot )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( ReportPlayerUI(null, viewer, player.uniqueId, currentReason) )
        }
        add( comp )
    }

    private fun previousPage()
    {
        if ( paginator.previousPage() )
        {
            updateFrame()
        }
    }

    private fun nextPage()
    {
        if ( paginator.nextPage() )
        {
            updateFrame()
        }
    }

}