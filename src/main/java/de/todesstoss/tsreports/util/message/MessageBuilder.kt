package de.todesstoss.tsreports.util.message

import de.todesstoss.tsreports.TSReports
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import java.util.*

class MessageBuilder(
    private var path: String
) {

    private val configManager = TSReports.instance.configManager
    private val message = Message()

    private var placeholders: java.util.function.Function<String, String>? = null
    private var hoverEvent: HoverEvent? = null
    private var clickEvent: ClickEvent? = null

    fun path(
        path: String
    ): MessageBuilder
    {
        this.path = path
        return this
    }

    fun hoverEvent(
        hoverEvent: HoverEvent
    ): MessageBuilder
    {
        this.hoverEvent = hoverEvent
        return this
    }

    fun clickEvent(
        clickEvent: ClickEvent
    ): MessageBuilder
    {
        this.clickEvent = clickEvent
        return this
    }

    fun placeholders(
        placeholders: java.util.function.Function<String, String>
    ): MessageBuilder
    {
        this.placeholders = placeholders
        return this
    }

    fun send(
        uuid: UUID?
    ) {
        this.message.receiver = uuid
        val message = ComponentBuilder( if ( placeholders == null ) configManager.getMessage(path, uuid) else configManager.getMessage(path, uuid, placeholders!!) ).currentComponent

        if ( hoverEvent == null && clickEvent == null )
        {
            this.message.message = message
            this.message.send()
            return
        }

        if ( hoverEvent != null )
            message.hoverEvent = hoverEvent

        if ( clickEvent != null )
            message.clickEvent = clickEvent

        val finalMessage = message
        this.message.message = finalMessage
        this.message.send()
    }

    fun get(
        uuid: UUID?
    ): BaseComponent
    {
        val message = ComponentBuilder( if ( placeholders == null ) configManager.getMessage(path, uuid) else configManager.getMessage(path, uuid, placeholders!!) ).currentComponent

        if ( hoverEvent == null && clickEvent == null )
            return message

        if ( hoverEvent != null )
            message.hoverEvent = hoverEvent

        if ( clickEvent != null )
            message.clickEvent = clickEvent

        return message
    }

    inner class Message {

        var message: BaseComponent = ComponentBuilder("").currentComponent
        var receiver: UUID? = null

        fun send()
        {
            if ( message.toLegacyText().isBlank() )
                return

            if ( receiver == null )
            {
                TSReports.instance.server.consoleSender
                    .spigot().sendMessage( message )
                return
            }

            TSReports.instance.server.getPlayer( receiver!! )!!
                .spigot().sendMessage( message )
        }

    }

}
