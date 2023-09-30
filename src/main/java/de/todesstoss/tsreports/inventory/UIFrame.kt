package de.todesstoss.tsreports.inventory

import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

abstract class UIFrame(
    open val parent: UIFrame?,
    open val viewer: Player
) {

    val components = ConcurrentHashMap.newKeySet<UIComponent>()!!

    abstract val title: String

    abstract val size: Int

    abstract val border: Boolean

    abstract fun createComponents()

    fun updateFrame()
    {
        InventoryDrawer.open(this)
    }

    fun getComponent(
        slot: Int
    ): UIComponent?
    {
        return components.stream()
            .filter { it.slot == slot }
            .findAny()
            .getOrNull()
    }

    fun add(
        component: UIComponent
    ) {
        components.add( component )
    }

    fun clear()
    {
        components.clear()
    }

    override fun equals(
        other: Any?
    ): Boolean
    {
        if ( other is UIFrame )
            return size == other.size && title == other.title
        return false
    }

    override fun hashCode(): Int
    {
        var result = parent?.hashCode() ?: 0
        result = 31 * result + viewer.hashCode()
        result = 31 * result + components.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + size
        return result
    }

}