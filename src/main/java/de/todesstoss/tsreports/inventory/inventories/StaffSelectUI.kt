package de.todesstoss.tsreports.inventory.inventories

import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.inventory.inventories.admin.AdminPanel
import de.todesstoss.tsreports.inventory.inventories.manage.ManageReportsUI
import de.todesstoss.tsreports.util.inventory.Messages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class StaffSelectUI(
    override val parent: UIFrame?,
    override val viewer: Player
) : UIFrame(parent, viewer) {

    override val title: String
        get() = Messages.GUI_STAFFSELECT_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 3

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        addManageReports()
        addAdminPanel()
    }

    private fun addManageReports()
    {
        val lore = Messages.GUI_STAFFSELECT_MANAGEREPORTS_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_STAFFSELECT_MANAGEREPORTS_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljNDVhMjRhYWFiZjQ5ZTIxN2MxNTQ4MzIwNDg0OGE3MzU4MmFiYTdmYWUxMGVlMmM1N2JkYjc2NDgyZiJ9fX0=")
            .lore( lore )
            .slot( 12 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( ManageReportsUI(this, viewer) )
        }
        add( comp )
    }

    private fun addAdminPanel()
    {
        val lore = Messages.GUI_STAFFSELECT_ADMINPANEL_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_STAFFSELECT_ADMINPANEL_NAME.getString( viewer.uniqueId ))
            .skull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=")
            .lore( lore )
            .slot( 14 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( AdminPanel(this, viewer) )
        }
        add( comp )
    }

}