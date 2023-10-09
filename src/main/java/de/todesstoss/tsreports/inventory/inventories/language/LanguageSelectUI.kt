package de.todesstoss.tsreports.inventory.inventories.language

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.inventory.Components
import de.todesstoss.tsreports.inventory.UIComponentImpl
import de.todesstoss.tsreports.inventory.UIFrame
import de.todesstoss.tsreports.util.Utils
import de.todesstoss.tsreports.util.inventory.Messages
import de.todesstoss.tsreports.util.message.MessageBuilder
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import kotlin.streams.toList

class LanguageSelectUI(
    override val parent: UIFrame?,
    override val viewer: Player
) : UIFrame(parent, viewer) {

    private val plugin = TSReports.instance

    private val viewerLocale = plugin.playerCache.get( viewer.uniqueId )?.language
        ?: plugin.configManager.defaultLocale

    override val title: String
        get() = Messages.GUI_LANGUAGESELECTOR_TITLE.getString( viewer.uniqueId )
            .replace("%language%", viewerLocale.getDisplayLanguage( viewerLocale ))

    override val size: Int
        get() = 9 * 6

    override val border: Boolean
        get() = false

    override fun createComponents()
    {
        add( Components.back(parent, 49, viewer) )

        val localeNames = plugin.configManager.getAvailableLocales().stream()
            .map { it.toString() }.sorted().toList()

        for (i in 0..localeNames.size)
        {
            val comp = UIComponentImpl.Builder(Material.PAPER)
                .name( localeNames[i] )
                .slot(i)
                .build()
            comp.listener(ClickType.LEFT, ClickType.RIGHT) {
                val offlinePlayer = plugin.playerCache.get( viewer.uniqueId )
                    ?: return@listener

                val name = ChatColor.stripColor( comp.item.itemMeta?.displayName )
                    ?: return@listener

                val newLocale = Utils.stringToLocale( name )
                offlinePlayer.language = newLocale

                plugin.sqlManager.updateLanguage( offlinePlayer )
                plugin.playerCache.replace( viewer.uniqueId, offlinePlayer )

                MessageBuilder("commands.language.selected")
                    .placeholders { it.replace("%language%", newLocale.getDisplayLanguage( newLocale )) }
                    .send( viewer.uniqueId )
                updateFrame()
            }
            add( comp )
        }
    }

}