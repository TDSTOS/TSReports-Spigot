package de.todesstoss.tsreports.util.cloudnet

import eu.cloudnetservice.driver.permission.PermissionManagement
import eu.cloudnetservice.driver.registry.ServiceRegistry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CNUtils {

    private val permissionManagement = ServiceRegistry.first( PermissionManagement::class.java )

    fun hasPermission(
        sender: CommandSender,
        permission: String
    ): Boolean
    {
        if ( permission.isBlank() )
            return true

        if ( sender !is Player )
            return sender.hasPermission( permission )

        val user = permissionManagement.getOrCreateUser( sender.uniqueId, sender.name )
        return user.hasPermission( permission ).asBoolean()
    }

}