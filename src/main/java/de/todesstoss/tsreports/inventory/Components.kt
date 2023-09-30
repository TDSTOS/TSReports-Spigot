package de.todesstoss.tsreports.inventory

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.inventory.Paginator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

object Components {

    private val plugin = TSReports.instance

    fun barrier(
        slot: Int,
        viewer: Player
    ): UIComponent
    {
        val type = Material.valueOf( plugin.configManager.getMessage("gui.barrier.type", viewer.uniqueId).uppercase() )

        return UIComponentImpl.Builder( type )
            .name(Messages.GUI_BARRIER_NAME.getString( viewer.uniqueId ))
            .slot(slot)
            .build()
    }

    fun back(
        parent: UIFrame?,
        slot: Int,
        viewer: Player
    ): UIComponent
    {
        val back = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_BACKBUTTON_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19")
            .slot(slot)
            .build()
        back.listener(ClickType.LEFT, ClickType.RIGHT) {
            if ( parent == null )
            {
                viewer.closeInventory()
                return@listener
            }
            InventoryDrawer.open( parent )
        }
        return back
    }

    fun previousPage(
        slot: Int,
        listener: Runnable?,
        paginator: Paginator,
        viewer: Player
    ): UIComponent?
    {
        if ( !paginator.hasPreviousPage )
            return null

        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_PREVIOUS_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
            .slot(slot)
            .build()
        oneTimeListener(comp, listener)
        return comp
    }

    fun nextPage(
        slot: Int,
        listener: Runnable?,
        paginator: Paginator,
        viewer: Player
    ): UIComponent?
    {
        if ( !paginator.hasNextPage )
            return null

        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_NEXT_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
            .slot(slot)
            .build()
        oneTimeListener(comp, listener)
        return comp
    }

    private fun oneTimeListener(
        comp: UIComponent,
        listener: Runnable?
    ) {
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            listener?.run()
            comp.listener(ClickType.LEFT, ClickType.RIGHT, listener = null)
        }
    }

}
