package de.todesstoss.tsreports.inventory.inventories.manage

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.data.`object`.ReportStatus
import de.todesstoss.tsreports.inventory.Components
import de.todesstoss.tsreports.inventory.InventoryDrawer
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.inventory.Paginator
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import de.todesstoss.tsreports.util.report.ReportUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*
import kotlin.streams.toList

class ManageReportsUI(
    override val parent: UIFrame?,
    override val viewer: Player
) : UIFrame(parent, viewer) {

    private val plugin = TSReports.instance

    private val reports = plugin.reportCache.reports().values.stream()
        // sort by status: new -> in process -> processed
        .sorted { o1, o2 ->
            when {
                o1.status == ReportStatus.NEW && o2.status != ReportStatus.NEW -> -1
                o1.status != ReportStatus.NEW && o2.status == ReportStatus.NEW -> 1
                o1.status == ReportStatus.IN_PROCESS && o2.status == ReportStatus.PROCESSED -> -1
                o1.status == ReportStatus.PROCESSED && o2.status == ReportStatus.IN_PROCESS -> 1
                else -> 0
            }
        }
        .toList().toMutableList()

    private val paginator = Paginator( size - 9, reports )

    override val title: String
        get() = Messages.GUI_MANAGEREPORTS_TITLE.getString( viewer.uniqueId )

    override val size: Int
        get() = 9 * 6

    override val border: Boolean
        get() = false

    override fun createComponents()
    {
        // 45       47      49      51
        //  S  []   <-   []  X  []  -> [] []
        // sort  previous   back   next
        add( Components.back(parent, 49, viewer) )

        var slot = 0
        var index = paginator.minIndex

        while ( paginator.isValidIndex( index ) )
        {
            val report = reports[index]
            addReport( slot, report )

            slot++
            index++
        }

        // TODO: add sort button

        val previous = Components.previousPage(47, this::previousPage, paginator, viewer)
        if ( previous != null ) add( previous )

        val next = Components.nextPage(51, this::nextPage, paginator, viewer)
        if ( next != null ) add( next )
    }

    private fun addReport(
        slot: Int,
        report: ReportFile
    ) {
        val lore = Messages.GUI_MANAGEREPORTS_REPORT_LORE.getList( viewer.uniqueId )
            .stream().map { string -> ReportUtils.replaceString( string, report ) }
            .toList()
        val component = UIComponentImpl.Builder(Material.PLAYER_HEAD)
            .name(Messages.GUI_MANAGEREPORTS_REPORT_NAME.getString( viewer.uniqueId )
                .replace("%id%", "${report.id}"))
            .skull( report.uniqueId.asOfflinePlayer() )
            .lore( lore )
            .slot( slot )
            .build()
        component.listener( ClickType.LEFT ) {
            if ( report.status == ReportStatus.PROCESSED &&
                !PlayerUtils.hasPermission(viewer, "tsreports.admin") )
            {
                MessageBuilder("report.alreadyProcessed")
                    .send( viewer.uniqueId )
                return@listener
            }

            if ( report.status == ReportStatus.IN_PROCESS && report.processing != viewer.uniqueId &&
                !PlayerUtils.hasPermission(viewer, listOf("tsreports.bypass.in_process", "tsreports.admin") )
            ) {
                MessageBuilder("report.inProcess")
                    .send(viewer.uniqueId)
                return@listener
            }

            InventoryDrawer.open( SpecificReportUI(this, viewer, report) )
        }
        component.listener( ClickType.RIGHT ) {
            if ( !PlayerUtils.hasPermission(viewer, "tsreports.delete") )
            {
                MessageBuilder("noPermission")
                    .send( viewer.uniqueId )
                return@listener
            }

            if ( report.status == ReportStatus.NEW &&
                !PlayerUtils.hasPermission(viewer, "tsreports.admin"))
            {
                MessageBuilder("report.isNew")
                    .send( viewer.uniqueId )
                return@listener
            }

            reports.remove( report )
            plugin.reportCache.remove( report.id )

            MessageBuilder("report.deleted")
                .placeholders { it.replace("%id%", "${report.id}") }
                .send( viewer.uniqueId )

            updateFrame()
        }
        component.confirmationRequired( ClickType.RIGHT )
        add( component )
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

private fun UUID.asOfflinePlayer(): OfflinePlayer?
{
    return TSReports.instance.playerCache.get( this )
}
