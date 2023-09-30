package de.todesstoss.tsreports.util.report

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.data.`object`.ReportStatus
import de.todesstoss.tsreports.inventory.UIComponent
import de.todesstoss.tsreports.inventory.UIComponentImpl
import org.bukkit.Material
import org.bukkit.entity.Player

object ReportUtils {

    private val plugin = TSReports.instance

    fun reasons(): Map<String, UIComponent>
    {
        val config = plugin.config
        val components = hashMapOf<String, UIComponent>()

        for ( i in 0..35 )
        {
            val path = "gui.reportPlayer.reasons.$i"
            if ( !config.contains(path) ) continue

            val name = config.getString("$path.name")!!
            val type = Material.valueOf( config.getString("$path.material")!!.uppercase() )
            val lore = config.getStringList("$path.lore")
            val slot = config.getInt("$path.slot")
            val reason = config.getString("$path.reason")!!
            val texture = if ( type == Material.PLAYER_HEAD ) config.getString("$path.texture")!! else ""

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
        return string.replace("%id%", "${report.id}")
            .replace("%username%", report.username)
            .replace("%uuid%", report.uniqueId.toString())
            .replace("%address%", report.address)
            .replace("%reason%", report.reason)
            .replace("%operator%", report.operator.toString())
            .replace("%server%", report.server)
            .replace("%status%", report.status.name)
            .replace("%processing%", report.processing.toString())
    }

}