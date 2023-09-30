package de.todesstoss.tsreports.inventory

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.inventory.inventories.ConfirmationUI
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import java.util.*

class InventoryController : Listener {

    private val plugin = TSReports.instance

    @EventHandler(
        priority = EventPriority.LOW,
        ignoreCancelled = true
    )
    fun onClose(
        event: InventoryCloseEvent
    ) {
        val uuid = event.player.uniqueId
        frames.remove( uuid )
    }

    @EventHandler(
        priority = EventPriority.LOW,
        ignoreCancelled = true
    )
    fun onInteract(
        event: InventoryClickEvent
    ) {
        val player = event.whoClicked

        val frame = frames[player.uniqueId]
            ?: return

        event.isCancelled = true

        if ( event.inventory.type == InventoryType.PLAYER )
            return

        val component = frame.getComponent( event.slot )
            ?: return

        val clickType = event.click
        var listener = component.listener( clickType )
            ?: return

        val permission = component.permission( clickType )
        if ( permission != null )
        {
            val entity = plugin.server.getPlayer( player.uniqueId )!!
            if ( !PlayerUtils.hasPermission( entity, permission ) )
            {
                MessageBuilder("noPermission")
                    .send( entity.uniqueId )
                return
            }
        }

        if ( component.isConfirmationRequired( clickType ) )
            listener = Runnable { InventoryDrawer.open( ConfirmationUI( frame, frame.viewer, component.listener( clickType )!! ) ) }

        val finalListener = listener
        plugin.scheduler.runTaskAsynchronously(plugin) { ->

            val meta = event.currentItem?.itemMeta

            if ( meta != null )
            {
                meta.lore = listOf("§7Loading...")
            }

            event.currentItem?.itemMeta = meta
            finalListener.run()
        }
    }

    companion object {

        private val frames = hashMapOf<UUID, UIFrame>()

        fun register(
            frame: UIFrame
        ) {
            frames[frame.viewer.uniqueId] = frame
        }

    }

}