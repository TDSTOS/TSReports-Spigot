package de.todesstoss.tsreports.event

import de.todesstoss.tsreports.data.`object`.ReportFile
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ReportEvent(
    val report: ReportFile,
    private var cancelled: Boolean
) : Event(), Cancellable {

    private var handlerList = HandlerList()

    override fun isCancelled(): Boolean
    {
        return cancelled
    }

    override fun setCancelled(
        cancel: Boolean
    ) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList
    {
        return handlerList
    }

}