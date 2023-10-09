package de.todesstoss.tsreports.data.`object`

import de.todesstoss.tsreports.TSReports
import org.bukkit.entity.Player
import java.util.*

data class OfflinePlayer(
    val uniqueId: UUID,
    var username: String,
    var address: String,
    var language: Locale,
    var loggedIn: Boolean = false,
    var isAdmin: Boolean = false
) {

    constructor(
        player: Player
    ) : this(
        player.uniqueId,
        player.name,
        player.address.toString().substring(1).split(":")[0]
    )

    constructor(
        uniqueId: UUID,
        username: String,
        address: String
    ) : this(
        uniqueId,
        username,
        address,
        TSReports.instance.configManager.defaultLocale
    )

    fun asPlayer(): Player?
    {
        return TSReports.instance.server
            .getPlayer( uniqueId )
    }

}