package de.todesstoss.tsreports.inventory.inventories.manage

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.data.`object`.ReportStatus
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponent
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.inventory.inventories.SpecificOptionsUI
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import de.todesstoss.tsreports.util.report.ReportUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.UUID
import kotlin.streams.toList

class SpecificReportUI(
    override val parent: UIFrame?,
    override val viewer: Player,
    private val report: ReportFile
) : UIFrame(parent, viewer) {

    private val plugin = TSReports.instance

    override val title: String
        get() = Messages.GUI_SPECIFICREPORT_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 5

    override val border: Boolean
        get() = true

    override fun createComponents()
    {
        addInfo()
        addChangeStatus()
        addClaim()
    }

    private fun addClaim()
    {
        val lore = Messages.GUI_SPECIFICREPORT_CLAIM_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder( Material.GREEN_DYE )
            .name( Messages.GUI_SPECIFICREPORT_CLAIM_NAME.getString( viewer.uniqueId ) )
            .lore( lore )
            .slot( 24 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT)
        {
            if ( report.processing != null && report.processing != viewer.uniqueId &&
                !PlayerUtils.hasPermission(viewer, "tsreports.admin") )
            {
                MessageBuilder("report.isClaimed")
                    .send( viewer.uniqueId )
                return@listener
            }

            if ( report.processing == viewer.uniqueId )
            {
                MessageBuilder("report.alreadyClaimed")
                    .send( viewer.uniqueId )
                return@listener
            }

            report.processing = viewer.uniqueId
            plugin.sqlManager.updateProcessing( report )
            plugin.reportCache.reports().replace( report.id, report )

            MessageBuilder("report.claimed")
                .placeholders { it.replace("%id%", "${report.id}") }
                .send( viewer.uniqueId )
        }
        add( comp )
    }

    private fun addChangeStatus()
    {
        val lore = Messages.GUI_SPECIFICREPORT_CHANGESTATUS_LORE.getList( viewer.uniqueId )
        val comp = UIComponentImpl.Builder( ReportUtils.statusItem( report ) )
            .name( Messages.GUI_SPECIFICREPORT_CHANGESTATUS_NAME.getString( viewer.uniqueId )
                .replace("%status%", report.status.name) )
            .lore( lore )
            .slot( 22 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT)
        {
            InventoryDrawer.open( SpecificOptionsUI( this, viewer, 9 * 3, statusComps() ) )
        }
        add( comp )
    }

    private fun addInfo()
    {
        val lore = Messages.GUI_SPECIFICREPORT_INFO_LORE.getList( viewer.uniqueId )
            .stream().map { ReportUtils.replaceString( it, report ) }.toList()
        val comp = UIComponentImpl.Builder( Material.WHITE_BANNER )
            .name( Messages.GUI_SPECIFICREPORT_INFO_NAME.getString( viewer.uniqueId )
                .replace("%id%", "${report.id}") )
            .lore( lore )
            .slot( 20 )
            .build()
        add( comp )
    }

    private fun statusComps(): List<UIComponent>
    {
        val list = arrayListOf<UIComponent>()

        var lore = Messages.GUI_SPECIFICREPORT_CHANGESTATUS_INPROCESS_LORE.getList( viewer.uniqueId )
        var comp = UIComponentImpl.Builder( Material.YELLOW_STAINED_GLASS_PANE )
            .name( Messages.GUI_SPECIFICREPORT_CHANGESTATUS_INPROCESS_NAME.getString( viewer.uniqueId ) )
            .lore( lore )
            .slot( 11 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT)
        {
            report.status = ReportStatus.IN_PROCESS
            plugin.sqlManager.updateStatus( report )
            plugin.reportCache.reports().replace( report.id, report )

            MessageBuilder("report.statusChanged")
                .placeholders { it.replace("%status%", report.status.name) }
                .send( viewer.uniqueId )
        }
        list.add( comp )

        lore = Messages.GUI_SPECIFICREPORT_CHANGESTATUS_PROCESSED_LORE.getList( viewer.uniqueId )
        comp = UIComponentImpl.Builder( Material.BLACK_STAINED_GLASS_PANE )
            .name( Messages.GUI_SPECIFICREPORT_CHANGESTATUS_PROCESSED_NAME.getString( viewer.uniqueId ) )
            .lore( lore )
            .slot( 15 )
            .build()
        comp.listener(ClickType.LEFT, ClickType.RIGHT)
        {
            report.status = ReportStatus.PROCESSED
            plugin.sqlManager.updateStatus( report )
            plugin.reportCache.reports().replace( report.id, report )

            MessageBuilder("report.statusChanged")
                .placeholders { it.replace("%status%", report.status.name) }
                .send( viewer.uniqueId )

            if ( PlayerUtils.isOnline( report.operator ) )
                MessageBuilder("report.processed")
                    .placeholders { it.replace("%name%", report.username) }
                    .send( report.operator )
            else processedList[report.operator] = report
        }
        list.add( comp )

        return list
    }

    companion object {
        val processedList = hashMapOf<UUID, ReportFile>()
    }

}