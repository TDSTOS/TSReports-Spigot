package de.todesstoss.tsreports.data.cache

import de.todesstoss.tsreports.data.manager.SqlManager
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class PlayerCache(
    private val sqlManager: SqlManager
) {

    private val players = hashMapOf<UUID, OfflinePlayer>()

    init {
        players.putAll( sqlManager.offlinePlayers() )
    }

    fun add(
        player: Player
    ): OfflinePlayer
    {
        if ( players.containsKey( player.uniqueId ) )
            return players[player.uniqueId]!!

        val offlinePlayer = OfflinePlayer( player )
        sqlManager.addOfflinePlayer( offlinePlayer )
        players[player.uniqueId] = offlinePlayer
        return offlinePlayer
    }

    fun get(
        uuid: UUID
    ): OfflinePlayer?
    {
        return players[uuid]
    }

    fun get(
        name: String
    ): OfflinePlayer?
    {
        return players.values.stream()
            .filter { it.username == name }
            .findAny()
            .getOrNull()
    }

    fun has(
        uuid: UUID
    ): Boolean
    {
        return players.containsKey( uuid )
    }

    fun remove(
        uuid: UUID
    ): OfflinePlayer?
    {
        sqlManager.removeOfflinePlayer( uuid )
        return players.remove( uuid )
    }

    fun replace(
        uuid: UUID,
        offlinePlayer: OfflinePlayer
    ) {
        players.replace( uuid, offlinePlayer )
    }

}