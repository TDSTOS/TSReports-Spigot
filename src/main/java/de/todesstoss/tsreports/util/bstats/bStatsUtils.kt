package de.todesstoss.tsreports.util.bstats

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportStatus
import org.bstats.bukkit.Metrics
import org.bstats.charts.SingleLineChart

object bStatsUtils {

    private val plugin = TSReports.instance

    fun initialize()
    {
        if ( !plugin.config.getBoolean("settings.bStats") )
            return

        // bStats information collected:
        //
        // - current reports
        // - open reports
        // - reports in process
        // - processed reports

        val metrics = Metrics(plugin, 19921)
        val reports = plugin.reportCache.reports()

        metrics.addCustomChart(SingleLineChart("current_reports") {
            return@SingleLineChart reports.size
        })

        metrics.addCustomChart(SingleLineChart("open_reports") {
            return@SingleLineChart reports.values.stream().filter { it.status == ReportStatus.NEW }.count().toInt()
        })

        metrics.addCustomChart(SingleLineChart("reports_in_process") {
            return@SingleLineChart reports.values.stream().filter { it.status == ReportStatus.IN_PROCESS }.count().toInt()
        })

        metrics.addCustomChart(SingleLineChart("processed_reports") {
            return@SingleLineChart reports.values.stream().filter { it.status == ReportStatus.PROCESSED }.count().toInt()
        })
    }

}