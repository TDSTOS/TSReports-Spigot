package de.todesstoss.tsreports.inventory

import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class UIComponent {

    private val listeners = hashMapOf<ClickType, Runnable>()
    private val permissions = hashMapOf<ClickType, String>()
    private val confirmationRequired = hashSetOf<ClickType>()

    abstract var item: ItemStack

    abstract var slot: Int

    fun listener(
        vararg click: ClickType,
        listener: Runnable?
    ) {
        click.forEach {
            listener(it, listener)
        }
    }

    fun listener(
        click: ClickType,
        listener: Runnable?
    ) {
        if ( listener == null )
            listeners.remove( click )
        else
            listeners[click] = listener
    }

    fun listener(
        click: ClickType
    ): Runnable?
    {
        return listeners[click]
    }

    fun permission(
        click: ClickType,
        permission: String
    ) {
        permissions[click] = permission
    }

    fun permission(
        click: ClickType
    ): String?
    {
        return permissions[click]
    }

    fun confirmationRequired(
        click: ClickType
    ) {
        confirmationRequired.add( click )
    }

    fun confirmationRequired(
        vararg clicks: ClickType
    ) {
        clicks.forEach { confirmationRequired(it) }
    }

    fun isConfirmationRequired(
        click: ClickType
    ): Boolean
    {
        return confirmationRequired.contains( click )
    }

}