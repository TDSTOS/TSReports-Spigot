package de.todesstoss.tsreports.data.`object`

import de.todesstoss.tsreports.TSReports
import java.util.*

data class PlayerLocale(
    val uuid: UUID?
) {

    private val plugin = TSReports.instance

    fun locale(): Locale
    {
        if ( uuid == null )
            return plugin.configManager.defaultLocale

        return plugin.playerCache.get( uuid )?.language
            ?: plugin.configManager.defaultLocale
    }

}
