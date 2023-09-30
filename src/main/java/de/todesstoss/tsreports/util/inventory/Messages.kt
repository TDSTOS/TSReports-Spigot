package de.todesstoss.tsreports.util.inventory

import de.todesstoss.tsreports.TSReports
import java.util.*

enum class Messages(
    val path: String
) {
    GUI_BARRIER_NAME("gui.barrier.name"),
    GUI_BACKBUTTON_NAME("gui.backbutton.name"),
    GUI_PREVIOUS_NAME("gui.previous.name"),
    GUI_NEXT_NAME("gui.next.name"),
    GUI_CONFIRMATION_TITLE("gui.confirmation.title"),
    GUI_CONFIRMATION_CONFIRM_NAME("gui.confirmation.confirm.name"),
    GUI_CONFIRMATION_RETURN_NAME("gui.confirmation.return.name"),
    GUI_MANAGEREPORTS_TITLE("gui.manageReports.title"),
    GUI_MANAGEREPORTS_REPORT_NAME("gui.manageReports.item.name"),
    GUI_MANAGEREPORTS_REPORT_LORE("gui.manageReports.item.lore"),
    GUI_SPECIFICREPORT_TITLE("gui.specificReport.title"),
    GUI_SPECIFICREPORT_INFO_NAME("gui.specificReport.info.name"),
    GUI_SPECIFICREPORT_INFO_LORE("gui.specificReport.info.lore"),
    GUI_SPECIFICREPORT_CHANGESTATUS_NAME("gui.specificReport.changeStatus.name"),
    GUI_SPECIFICREPORT_CHANGESTATUS_LORE("gui.specificReport.changeStatus.lore"),
    GUI_SPECIFICREPORT_CHANGESTATUS_INPROCESS_NAME("gui.specificReport.changeStatus.inProcess.name"),
    GUI_SPECIFICREPORT_CHANGESTATUS_INPROCESS_LORE("gui.specificReport.changeStatus.inProcess.lore"),
    GUI_SPECIFICREPORT_CHANGESTATUS_PROCESSED_NAME("gui.specificReport.changeStatus.processed.name"),
    GUI_SPECIFICREPORT_CHANGESTATUS_PROCESSED_LORE("gui.specificReport.changeStatus.processed.lore"),
    GUI_SPECIFICREPORT_CLAIM_NAME("gui.specificReport.claim.name"),
    GUI_SPECIFICREPORT_CLAIM_LORE("gui.specificReport.claim.lore"),
    GUI_REPORTPLAYER_TITLE("gui.reportPlayer.title"),
    GUI_REPORTPLAYER_PLAYER_NAME("gui.reportPlayer.item.name"),
    GUI_REPORTPLAYER_REASON_NAME("gui.reportPlayer.reason.name"),
    GUI_REPORTPLAYER_REASON_LORE("gui.reportPlayer.reason.lore"),
    GUI_REPORTPLAYER_CONFIRM_NAME("gui.reportPlayer.confirm.name"),
    GUI_PLAYERSELECT_TITLE("gui.playerSelect.title"),
    GUI_PLAYERSELECT_PLAYER_NAME("gui.playerSelect.player.name"),
    GUI_PLAYERSELECT_PLAYER_LORE("gui.playerSelect.player.lore"),
    GUI_REASONSELECT_TITLE("gui.reasonSelect.title"),
    GUI_REASONSELECT_SIZE("gui.reasonSelect.size");

    private val configManager = TSReports.instance.configManager

    fun getString(
        uuid: UUID?
    ): String
    {
        return configManager.getMessage( path, uuid )
    }

    fun getList(
        uuid: UUID?
    ): MutableList<String>
    {
        return configManager.getStringList( path, uuid ).toMutableList()
    }

    fun getInt(
        uuid: UUID?
    ): Int
    {
        return configManager.getInteger( path, uuid )
    }

}