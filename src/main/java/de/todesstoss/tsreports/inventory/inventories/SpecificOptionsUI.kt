package de.todesstoss.tsreports.inventory.inventories

import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponent
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class SpecificOptionsUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    override val size: Int,
    private val options: List<UIComponent>
) : UIFrame(parent, viewer) {

    override val title: String
        get() = Messages.GUI_CONFIRMATION_TITLE.getString( viewer.uniqueId )

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        val returnComp = UIComponentImpl.Builder(Material.RED_WOOL)
            .name( Messages.GUI_CONFIRMATION_RETURN_NAME.getString( viewer.uniqueId ) )
            .slot( 17 )
            .build()
        returnComp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( parent )
        }
        add( returnComp )

        options.forEach { add( it ) }
    }

}