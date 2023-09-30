package de.todesstoss.tsreports.data.cache

import de.todesstoss.tsreports.data.manager.SqlManager
import de.todesstoss.tsreports.data.`object`.ReportFile
import java.util.*

class ReportCache(
    private val sqlManager: SqlManager
) {

    private val reports = hashMapOf<Int, ReportFile>()

    init {
        reports.putAll( sqlManager.reports() )
    }

    fun add(
        report: ReportFile
    ) {
        if ( reports.containsKey( report.id ) )
            return

        sqlManager.addReport( report )
        reports[report.id] = report
    }

    fun get(
        id: Int
    ): ReportFile?
    {
        return reports[id]
    }

    fun update(
        uuid: UUID
    ) {
        reports.values.removeIf { it.uniqueId == uuid }

        val uuidReports = sqlManager.reports( uuid )
        val uuidReportsMap = uuidReports.associateBy { it.id }

        reports.putAll( uuidReportsMap )
    }

    fun newest(
        uuid: UUID
    ): ReportFile
    {
        return reports.values.stream()
            .filter { it.uniqueId == uuid }
            .max( Comparator.comparingLong { it.timeOfReport } )
            .orElse( null )
    }

    fun has(
        id: Int
    ): Boolean
    {
        return reports.containsKey( id )
    }

    fun remove(
        id: Int
    ): ReportFile?
    {
        sqlManager.removeReport( id )
        return reports.remove( id )
    }

    fun reports(): HashMap<Int, ReportFile>
    {
        return HashMap( reports )
    }

    fun size(): Int
    {
        return reports.size
    }

}