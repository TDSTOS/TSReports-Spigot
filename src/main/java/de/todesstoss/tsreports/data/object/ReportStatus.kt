package de.todesstoss.tsreports.data.`object`

enum class ReportStatus(
    val defaultPath: String
) {
    NEW("status.new"),
    IN_PROCESS("status.inProcess"),
    PROCESSED("status.processed")
}