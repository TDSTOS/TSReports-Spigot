package de.todesstoss.tsreports.util.report

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.data.`object`.ReportStatus
import de.todesstoss.tsreports.inventory.UIComponent
import de.todesstoss.tsreports.inventory.UIComponentImpl
import org.bukkit.Material
import java.util.*

object ReportUtils {

    private val plugin = TSReports.instance

    fun reasons(
        uuid: UUID
    ): Map<String, UIComponent>
    {
        val configManager = plugin.configManager
        val components = hashMapOf<String, UIComponent>()

        for ( i in 0..35 )
        {
            val path = "gui.reasonSelect.reasons.$i"
            if ( !configManager.contains(path, uuid) ) continue

            val name = configManager.getMessage("$path.name", uuid)
            val type = Material.valueOf( configManager.getMessage("$path.material", uuid).uppercase() )
            val slot = configManager.getInteger("$path.slot", uuid)
            val reason = configManager.getMessage("$path.reason", uuid)
            val texture = if ( type == Material.PLAYER_HEAD ) configManager.getMessage("$path.texture", uuid) else ""
            val lore = configManager.getStringList("$path.lore", uuid)

            components[reason] = UIComponentImpl.Builder(type)
                .skull( texture )
                .lore( lore )
                .name( name )
                .slot( slot )
                .build()
        }

        return components
    }

    fun randomId(): Int
    {
        val random = ( 100000..999999 ).random()
        if ( plugin.reportCache.has( random ) )
            return randomId()
        return random
    }

    fun statusItem(
        report: ReportFile
    ): Material
    {
        return when(report.status)
        {
            ReportStatus.NEW -> Material.GREEN_STAINED_GLASS_PANE
            ReportStatus.IN_PROCESS -> Material.YELLOW_STAINED_GLASS_PANE
            ReportStatus.PROCESSED -> Material.BLACK_STAINED_GLASS_PANE
        }
    }

    fun replaceString(
        string: String,
        report: ReportFile
    ): String
    {
        val operator = if ( !plugin.playerCache.has( report.operator ) ) report.operator.toString()
            else plugin.playerCache.get( report.operator )!!.username

        val processing = if ( report.processing == null ) "N/A"
            else if ( !plugin.playerCache.has( report.processing!! ) ) report.processing.toString()
            else plugin.playerCache.get( report.processing!! )!!.username

        return string.replace("%id%", "${report.id}")
            .replace("%username%", report.username)
            .replace("%uuid%", report.uniqueId.toString())
            .replace("%address%", report.address)
            .replace("%reason%", report.reason)
            .replace("%operator%", operator)
            .replace("%server%", report.server)
            .replace("%status%", report.status.name)
            .replace("%processing%", processing)
    }

}