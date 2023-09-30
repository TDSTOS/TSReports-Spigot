package de.todesstoss.tsreports.inventory

import de.todesstoss.tsreports.TSReports
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object InventoryDrawer {

    private val plugin = TSReports.instance
    private val opening = ConcurrentHashMap<UUID, UIFrame>()

    fun open(
        frame: UIFrame?
    ) {
        if ( frame == null )
            return

        val uuid = frame.viewer.uniqueId
        if ( frame == opening[uuid] )
            return

        opening[uuid] = frame
        plugin.scheduler.runTaskAsynchronously(plugin) { ->
            val inventory = prepareInventory( frame )

            if ( frame != opening[uuid] )
                return@runTaskAsynchronously

            plugin.scheduler.runTaskAsynchronously(plugin) { ->
                frame.viewer.openInventory( inventory )
                InventoryController.register( frame )
                opening.remove( uuid )
            }
        }
    }

    private fun prepareInventory(
        frame: UIFrame
    ): Inventory
    {
        val inventory = Bukkit.createInventory( frame.viewer, frame.size, frame.title )
        val start = System.currentTimeMillis()
        createBorder( inventory, frame )
        setComponents( inventory, frame )

        plugin.debug("It took ${System.currentTimeMillis() - start} millisecond(s) to load the frame" +
                " ${frame.title} for ${frame.viewer.name}")

        return inventory
    }

    private fun setComponents(
        inventory: Inventory,
        frame: UIFrame
    ) {
        frame.clear()
        try {
            frame.createComponents()
        } catch (ex: NoSuchFieldError) {
            return
        }

        val components = frame.components
        if ( components.isEmpty() )
        {
            plugin.logger.warning("Frame ${frame.title} has no components.")
            return
        }
        for (comp in frame.components)
        {
            if ( comp.slot >= frame.size )
                continue
            inventory.setItem( comp.slot, comp.item )
        }
    }

    private fun createBorder(
        inventory: Inventory,
        frame: UIFrame
    ) {
        if ( !frame.border )
            return

        val border = Components.barrier(0, frame.viewer)
        val frameSize = frame.size

        // Top
        for ( i in 0..8 )
            inventory.setItem( i, border.item )

        // Bottom
        for ( i in frameSize - 9 until frameSize )
            inventory.setItem( i, border.item )

        // Left
        for ( i in 9 until frameSize - 9 step 9 )
            inventory.setItem(i, border.item)

        // Right
        for ( i in 17 until frameSize step 9 )
            inventory.setItem(i, border.item)
    }

}