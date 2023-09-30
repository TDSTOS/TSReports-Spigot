package de.todesstoss.tsreports.inventory.inventories

import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class ConfirmationUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    private val listener: Runnable
) : UIFrame(parent, viewer) {

    override val title: String
        get() = Messages.GUI_CONFIRMATION_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 3

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        val confirmComp = UIComponentImpl.Builder(Material.LIME_DYE)
            .name(Messages.GUI_CONFIRMATION_CONFIRM_NAME.getString( viewer.uniqueId )).slot(12).build()
        confirmComp.listener(ClickType.LEFT, ClickType.RIGHT, listener = listener)
        add(confirmComp)

        val returnComp = UIComponentImpl.Builder(Material.RED_DYE)
            .name(Messages.GUI_CONFIRMATION_RETURN_NAME.getString( viewer.uniqueId )).slot(14).build()
        returnComp.listener(ClickType.LEFT, ClickType.RIGHT) { InventoryDrawer.open( parent ) }
        add(returnComp)
    }

}