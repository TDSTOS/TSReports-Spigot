package de.todesstoss.tsreports.inventory

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.todesstoss.tsreports.data.`object`.OfflinePlayer
import de.todesstoss.tsreports.util.Utils
import de.todesstoss.tsreports.util.fetcher.SkinFetcher
import de.todesstoss.tsreports.util.reflection.ReflectionUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.streams.toList

class UIComponentImpl : UIComponent() {

    override var item = ItemStack( Material.STONE )

    override var slot = 0

    class Builder {

        private val component = UIComponentImpl()
        private val lore = mutableListOf<String>()

        private var headUuid = UUID.fromString("06be0255-1acd-4442-91df-68b44f1c61c6")
        private var texture = ""

        constructor(
            material: Material
        ) : this( ItemStack(material) )

        constructor(
            item: ItemStack
        ) {
            component.item = item
        }

        fun name(
            displayName: String
        ): Builder
        {
            val meta = component.item.itemMeta
            meta?.setDisplayName( displayName )
            component.item.itemMeta = meta
            return this
        }

        fun lore(
            lore: List<String>
        ): Builder
        {
            this.lore.clear()
            this.lore.addAll( lore.stream().map(Utils::color).toList() )
            return this
        }

        fun line(
            line: String
        ): Builder
        {
            this.lore.add( Utils.color( line ) )
            return this
        }

        fun slot(
            slot: Int
        ): Builder
        {
            component.slot = slot
            return this
        }

        fun skull(
            textureOrName: String
        ): Builder
        {
            this.texture = when {
                textureOrName.length <= 16 -> SkinFetcher.from( textureOrName ).texture
                else -> textureOrName
            }
            return this
        }

        fun skull(
            player: OfflinePlayer?
        ): Builder
        {
            if ( player == null )
            {
                this.texture = SkinFetcher.STEVE.texture
                return this
            }

            this.texture = SkinFetcher.from( player.username ).texture
            this.headUuid = player.uniqueId
            return this
        }

        fun build(): UIComponent
        {
            val meta = component.item.itemMeta
            meta?.lore = this.lore

            if ( meta is SkullMeta )
            {
                val profile = GameProfile(headUuid, null)
                profile.properties.put("textures", Property("textures", this.texture))
                ReflectionUtils.setField(meta, "profile", profile)
            }

            component.item.itemMeta = meta
            return component
        }

    }

}