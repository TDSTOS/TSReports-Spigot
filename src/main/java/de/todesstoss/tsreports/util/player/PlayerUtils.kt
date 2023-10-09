package de.todesstoss.tsreports.util.player

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

object PlayerUtils {

    private val plugin = TSReports.instance

    fun isOnline(
        uuid: UUID
    ): Boolean
    {
        return Bukkit.getPlayer( uuid )?.isOnline == true
    }

    fun hasPermission(
        player: Player,
        permission: String
    ): Boolean
    {
        return hasPermission( player.uniqueId, listOf( permission ) )
    }

    fun hasPermission(
        player: Player,
        permissions: List<String>
    ): Boolean
    {
        return hasPermission( player.uniqueId, permissions )
    }

    fun hasPermission(
        uuid: UUID,
        permission: String
    ): Boolean
    {
        return hasPermission( uuid, listOf( permission ) )
    }

    fun hasPermission(
        uuid: UUID?,
        permissions: List<String>
    ): Boolean
    {
        if ( permissions.isEmpty() || permissions.stream().filter { it.isNotBlank() }.count() == 0L )
            return true

        var bool = false
        for ( perm in permissions )
        {
            if ( bool )
                continue

            if ( uuid == null )
            {
                bool = Bukkit.getConsoleSender().hasPermission( perm )
                continue
            }

            val player = Bukkit.getPlayer( uuid )
                ?: return false

            if ( plugin.isCloudNet )
            {
                bool = plugin.cloudNet.hasPermission( player, perm )
                continue
            }

            if ( plugin.isLuckPerms )
            {
                bool = plugin.luckPerms.getPlayerAdapter( Player::class.java ).getUser( player )
                    .cachedData.permissionData.checkPermission( perm ).asBoolean()
                continue
            }

            bool = player.hasPermission( perm )
        }
        return bool
    }

}

fun Player.asOfflinePlayer(): OfflinePlayer
{
    return TSReports.instance.playerCache.get( this.uniqueId )!!
}
