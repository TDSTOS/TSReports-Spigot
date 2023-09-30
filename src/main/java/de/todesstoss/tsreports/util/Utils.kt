package de.todesstoss.tsreports.util

import org.bukkit.ChatColor
import java.util.*

object Utils {

    fun color(
        string: String
    ): String
    {
        return ChatColor.translateAlternateColorCodes('&', string)
    }

    fun stringToLocale(
        string: String
    ): Locale
    {
        val split = string.split("-", "_")
        return Locale( split[0], split[1] )
    }

}