package de.todesstoss.tsreports.data.`object`

import de.todesstoss.tsreports.util.report.ReportUtils
import org.bukkit.entity.Player
import java.sql.Time
import java.time.Instant
import java.util.*

data class ReportFile(
    val id: Int,
    val username: String,
    val uniqueId: UUID,
    val address: String,
    val reason: String,
    val operator: UUID,
    val server: String,
    var status: ReportStatus = ReportStatus.NEW,
    var processing: UUID? = null,
    val timeOfReport: Long = System.currentTimeMillis()
) {

    constructor(
        reported: OfflinePlayer,
        operator: UUID,
        reason: String
    ) : this(
        ReportUtils.randomId(),
        reported.username,
        reported.uniqueId,
        reported.address,
        reason,
        operator,
        reported.asPlayer()?.server?.name ?: "N/A"
    )

    constructor(
        reported: Player,
        operator: UUID,
        reason: String
    ) : this(
        ReportUtils.randomId(),
        reported.name,
        reported.uniqueId,
        reported.address.toString().substring(1).split(":")[0],
        reason,
        operator,
        reported.server.name
    )

}