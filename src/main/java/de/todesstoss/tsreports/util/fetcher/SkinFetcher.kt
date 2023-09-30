package de.todesstoss.tsreports.util.fetcher

import com.google.gson.JsonParser
import org.bukkit.entity.Player
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class SkinFetcher(
    val texture: String,
    val signature: String
) {

    companion object {

        val STEVE = SkinFetcher(
            "ewogICJ0aW1lc3RhbXAiIDogMTY2MTY3OTM3OTkwMCwKICAicHJvZmlsZUlkIiA6ICI4NjY3YmE3MWI4NWE0MDA0YWY1NDQ1N2E5" +
                    "NzM0ZWVkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGV2ZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR" +
                    "1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dH" +
                    "VyZS82MGE1YmQwMTZiM2M5YTFiOTI3MmU0OTI5ZTMwODI3YTY3YmU0ZWJiMjE5MDE3YWRiYmM0YTRkMjJlYmQ1YjEiCiAgICB9L" +
                    "AogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk1M2Nh" +
                    "YzhiNzc5ZmU0MTM4M2U2NzVlZTJiODYwNzFhNzE2NThmMjE4MGY1NmZiY2U4YWEzMTVlYTcwZTJlZDYiCiAgICB9CiAgfQp9",
            "ucw8h2rj66lroaieZON3VyQRbSLDdZzS/lOiAIrNbcKgsrKuD8UAiC9daUiRJ7aQCilOhCnnKVwwXX74PmHWL4nAPpPb903ZHwh" +
                    "mRS8Mshc0mPZrpvWfpkoZhDVdiFpqHSfGdwRweZyCifa39DRJstAuPmqdp3Ioyot6Rx4bCauXV8WQq0yMgP+oDrkx8O2aBr19h6" +
                    "6OXDl7Er28e8IgDnNHAcanSCNnWgV/RYiBqIzBmItLescpyTqCnVl3uYZfXVyEvNEy5IIBM6nhV4VoP9sf8Ld0AF6bXsSCaMbaJ" +
                    "8h99+jqCUlSGbFuMYlU8Ih0us9vvaAolqQasjvNxpXbN390mU3lw3NupCzSNNG+47Os5XeZ8C6nkY1kq3eHTNW/hDYwIi1A2TQv" +
                    "qvmeU0dMclV9L/Oj85YA1RbCYkK/3DuQBf10rek26EiOL1qyBL5A8jBorK5mR2ZWzDgWorN9XXclwPnadt+nN80m+fDuZBGS6yY" +
                    "Rljc/gqckTBprd055Vk93K3LmqBKS87nIILt9QRQO8x8rLYMZ4uz0Kwzagj/Gb4V7FHtZ4VMyzcqS9iYJfGGzBuvL72CzkAM96P" +
                    "YXO4PjSTgzQ4FwlMkNndcyt4TecwcrmfKpusW1j4i4QM092ktbDvMt9LFVCZkVRqnsiN/va5/RnNBfLJpcp7IzLLU="
        )

        fun from(
            player: Player
        ): SkinFetcher
        {
            return from( player.name )
        }

        fun from(
            playerName: String?
        ): SkinFetcher
        {
            require(playerName != null) { "PlayerName can not be null!" }

            var url = URL("https://api.mojang.com/users/profiles/minecraft/$playerName")
            var reader = InputStreamReader( url.openStream() )
            val uuid = JsonParser().parse( reader ).asJsonObject["id"].asString

            url = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
            reader = InputStreamReader( url.openStream() )
            val textureProperty = JsonParser().parse( reader ).asJsonObject["properties"].asJsonArray[0].asJsonObject
            val texture = textureProperty["value"].asString
            val signature = textureProperty["signature"].asString

            return SkinFetcher( texture, signature )
        }

    }

    val shortTexture: String

    init {
        val pattern = Pattern.compile("\"(http://textures\\.minecraft\\.net/texture/)(?<shortTexture>\\w+)\"")
        val matcher = pattern.matcher( String( Base64.getDecoder().decode( texture.toByteArray() ) ) )
        this.shortTexture = if ( matcher.find() ) matcher.group("shortTexture") else ""
    }

}