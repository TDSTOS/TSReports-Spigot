package de.todesstoss.tsreports.inventory.inventories.report

import de.todesstoss.tsreports.inventory.Components
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.inventory.Paginator
import de.todesstoss.tsreports.util.report.ReportUtils
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*

class ReasonSelectUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    private val target: UUID?
) : UIFrame(parent, viewer) {

    private val reasons = ReportUtils.reasons( viewer.uniqueId )
    private val paginator = Paginator( size - 9, reasons.size )

    override val title: String
        get() = Messages.GUI_REASONSELECT_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = Messages.GUI_REASONSELECT_SIZE.getInt( viewer.uniqueId )

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        add( Components.back(parent, size - 5, viewer) )

        var index = paginator.minIndex

        while ( paginator.isValidIndex( index ) )
        {
            val reason = reasons.keys.toList()[index]
            addReason( reason )

            index++
        }

        val previous = Components.previousPage(size - 7, this::previousPage, paginator, viewer)
        if ( previous != null ) add( previous )

        val next = Components.nextPage(size - 3, this::nextPage, paginator, viewer)
        if ( next != null ) add( next )
    }

    private fun addReason(
        reason: String
    ) {
        val comp = reasons[reason] ?: return
        comp.listener(ClickType.LEFT, ClickType.RIGHT) {
            InventoryDrawer.open( ReportPlayerUI(null, viewer, target, reason) )
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