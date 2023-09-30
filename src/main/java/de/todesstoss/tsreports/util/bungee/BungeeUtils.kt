package de.todesstoss.tsreports.util.bungee

import com.google.common.io.ByteStreams
import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.ReportFile
import de.todesstoss.tsreports.util.message.MessageBuilder
import de.todesstoss.tsreports.util.player.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID

class BungeeUtils : PluginMessageListener {

    private val plugin = TSReports.instance
    val updateList = arrayListOf<UUID>()

    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray
    ) {
        if ( channel != "TSReportsBungee" )
            return

        val `in` = ByteStreams.newDataInput( message )
        val subchannel = `in`.readUTF()

        if ( subchannel == "Forward" )
        {
            val len = `in`.readShort()
            val msgbytes = ByteArray( len.toInt() )
            `in`.readFully( msgbytes )

            val msgin = DataInputStream( ByteArrayInputStream( msgbytes ) )
            val string = msgin.readUTF()

            when(string.split(": ")[0])
            {
                "UPDATE" -> {
                    val uuid = UUID.fromString( string.split(": ")[1] )
                    plugin.reportCache.update( uuid )
                }

                "REPORT" -> {
                    val id = string.split(": ")[1].toInt()
                    Bukkit.getOnlinePlayers().stream()
                        .filter { PlayerUtils.hasPermission(it, listOf("tsreports.admin", "tsreports.notify")) }
                        .forEach {
                            MessageBuilder("report.notify")
                                .placeholders { s -> s.replace("%id%", "$id") }
                                .send( it.uniqueId )
                        }
                }
            }
        }
    }

    fun sendUpdate(
        player: Player
    ) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Forward")
        out.writeUTF("ALL")
        out.writeUTF("BungeeCord")

        val msgbytes = ByteArrayOutputStream()
        val msgout = DataOutputStream( msgbytes )

        msgout.writeUTF("UPDATE: ${player.uniqueId}")

        out.writeShort( msgbytes.toByteArray().size )
        out.write( msgbytes.toByteArray() )

        player.sendPluginMessage( plugin, "BungeeCord", out.toByteArray() )
    }

    fun sendNotification(
        player: Player,
        report: ReportFile
    ) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Forward")
        out.writeUTF("ALL")
        out.writeUTF("BungeeCord")

        val msgbytes = ByteArrayOutputStream()
        val msgout = DataOutputStream( msgbytes )

        msgout.writeUTF("REPORT: ${report.id}")

        out.writeShort( msgbytes.toByteArray().size )
        out.write( msgbytes.toByteArray() )

        player.sendPluginMessage( plugin, "BungeeCord", out.toByteArray() )
    }

}