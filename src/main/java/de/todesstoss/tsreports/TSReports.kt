package de.todesstoss.tsreports

import de.todesstoss.tsreports.data.cache.PlayerCache
import de.todesstoss.tsreports.data.cache.ReportCache
import de.todesstoss.tsreports.data.manager.ConfigManager
import de.todesstoss.tsreports.data.manager.SqlManager
import de.todesstoss.tsreports.inventory.InventoryController
import de.todesstoss.tsreports.listener.ConnectionListener
import de.todesstoss.tsreports.listener.ReportListener
import de.todesstoss.tsreports.util.bstats.bStatsUtils
import de.todesstoss.tsreports.util.bungee.BungeeUtils
import de.todesstoss.tsreports.util.cloudnet.CNUtils
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import java.io.InputStream

class TSReports : JavaPlugin() {

    companion object {
        lateinit var instance: TSReports
    }

    lateinit var configManager: ConfigManager
    lateinit var sqlManager: SqlManager
    lateinit var playerCache: PlayerCache
    lateinit var reportCache: ReportCache

    // CloudNet
    lateinit var cloudNet: CNUtils
    var isCloudNet: Boolean = false

    // Bungee
    lateinit var bungeeCord: BungeeUtils
    var isBungee: Boolean = false

    // LuckPerms
    lateinit var luckPerms: LuckPerms
    var isLuckPerms: Boolean = false

    override fun onEnable()
    {
        instance = this
        configManager = ConfigManager( this )
        sqlManager = SqlManager()
        playerCache = PlayerCache( sqlManager )
        reportCache = ReportCache( sqlManager )

        registerListener(
            InventoryController(),
            ConnectionListener(),
            ReportListener()
        )

        /*
         * bStats Initialization
         */
        logger.info("Initializing bStats...")
        bStatsUtils.initialize()

        /*
         * BungeeCord initialization
         */
        logger.info("Checking if server is subject to a BungeeCord network")
        isBungee = config.getBoolean("settings.bungeeCord")

        if ( isBungee )
        {
            logger.info("Initializing BungeeCord connection...")
            bungeeCord = BungeeUtils()

            Bukkit.getMessenger().registerIncomingPluginChannel( this, "BungeeCord", bungeeCord )
            Bukkit.getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" )
            logger.info("Initialized.")
        }

        /*
         * CloudNet initialization
         */
        logger.info("Checking if server is running via CloudNet")
        isCloudNet = config.getBoolean("settings.cloudNet")

        if ( isCloudNet )
        {
            logger.info("Initializing CloudNet connection...")
            cloudNet = CNUtils()
            logger.info("Initialized.")
        }

        /*
         * LuckPerms initialization
         */
        logger.info("Checking if server has plugin LuckPerms running")
        isLuckPerms = Bukkit.getServicesManager().isProvidedFor( LuckPerms::class.java )

        if ( isLuckPerms )
        {
            logger.info("Initializing LuckPerms connection...")
            luckPerms = Bukkit.getServicesManager().getRegistration( LuckPerms::class.java )!!.provider
            logger.info("Initialized.")
        }
    }

    override fun onDisable()
    {
        sqlManager.close()

        /*
         * BungeeCord termination
         */
        if ( isBungee )
        {
            logger.info("Terminating BungeeCord connection...")
            Bukkit.getMessenger().unregisterIncomingPluginChannel( this )
            Bukkit.getMessenger().unregisterOutgoingPluginChannel( this )
            logger.info("Terminated.")
        }
    }

    private fun registerListener(
        vararg listener: Listener
    ) {
        debug("Registering listener: ${listener.javaClass.simpleName}")
        listener.forEach { server.pluginManager.registerEvents( it, this ) }
    }

    fun stream(
        path: String
    ): InputStream?
    {
        debug("Getting file stream for path: $path")
        return javaClass.classLoader.getResourceAsStream( path )
    }

    fun debug(
        message: String
    ) {
        if ( !config.getBoolean("settings.debug", false) ) return
        logger.info( "[DEBUG] $message" )
    }

    val scheduler: BukkitScheduler
        get() = server.scheduler
    
    val config: FileConfiguration
        get() = configManager.config

}