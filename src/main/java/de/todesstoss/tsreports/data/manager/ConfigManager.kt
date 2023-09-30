package de.todesstoss.tsreports.data.manager

import de.todesstoss.tsreports.TSReports
import de.todesstoss.tsreports.data.`object`.PlayerLocale
import de.todesstoss.tsreports.util.Utils
import de.todesstoss.tsreports.util.file.FileUtils
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList
import kotlin.streams.toList

class ConfigManager(
    private val plugin: TSReports
) {

    lateinit var config: FileConfiguration
    lateinit var file: File
    private val locales = mutableMapOf<Locale, FileConfiguration>()
    lateinit var defaultLocale: Locale

    init {
        setup()
    }

    fun setup()
    {
        config = copyConfigFile()
        copyLocalesFromFolder()
        locales.putAll( getLocales() )
        defaultLocale = Utils.stringToLocale( config.getString("default-server-language")!! )
    }

    fun getInteger(
        path: String
    ): Int
    {
        var int: Int? = null

        if ( locales.containsKey(defaultLocale) )
            int = locales[defaultLocale]?.getInt(path)

        if ( int == null )
            int = 0

        return int
    }

    fun getInteger(
        path: String,
        uuid: UUID?
    ): Int
    {
        val locale = PlayerLocale(uuid).locale()
        var int: Int? = null

        if ( locales.containsKey(locale) )
            int = locales[locale]?.getInt(path)

        if ( int == null )
            int = getInteger(path)

        return int
    }

    fun getMessage(
        path: String
    ): String
    {
        var message: String? = null

        if ( locales.containsKey(defaultLocale) )
            message = locales[defaultLocale]?.getString(path)

        if ( message == null )
            message = path

        return Utils.color( message )
    }

    fun getMessage(
        path: String,
        uuid: UUID?
    ): String
    {
        val locale = PlayerLocale(uuid).locale()
        var message: String?
        var prefix = ""

        if ( locales.containsKey(locale) )
        {
            message = locales[locale]?.getString(path)
            if ( message == null )
                message = getMessage(path)
            prefix = locales[locale]?.getString("prefix")
                ?: locales[defaultLocale]?.getString("prefix") ?: ""
        }
        else message = getMessage(path)

        return Utils.color( message.replace("%prefix%", prefix) )
    }

    fun getMessage(
        path: String,
        placeholders: java.util.function.Function<String, String>
    ): String
    {
        val message = getMessage(path)
        return Utils.color( placeholders.apply( message ) )
    }

    fun getMessage(
        path: String,
        uuid: UUID?,
        placeholders: java.util.function.Function<String, String>
    ): String
    {
        val message = getMessage(path, uuid)
        return Utils.color( placeholders.apply( message ) )
    }

    fun getStringList(
        path: String
    ): List<String>
    {
        var list: List<String>? = null

        if ( locales.containsKey(defaultLocale) )
            list = locales[defaultLocale]?.getStringList(path)

        if ( list == null )
            list = listOf(path)

        return list.stream()
            .map(Utils::color)
            .toList()
    }

    fun getStringList(
        path: String,
        uuid: UUID?
    ): List<String>
    {
        val locale = PlayerLocale(uuid).locale()
        var list: List<String>? = null

        if ( locales.containsKey(locale) )
            list = locales[locale]?.getStringList(path)

        if ( list == null )
            list = getStringList(path)

        return list.stream()
            .map(Utils::color)
            .toList()
    }

    fun getStringList(
        path: String,
        placeholders: java.util.function.Function<String, String>
    ): List<String>
    {
        val list = getStringList(path)
        return list.stream()
            .map(placeholders::apply)
            .map(Utils::color)
            .toList()
    }

    fun getStringList(
        path: String,
        uuid: UUID?,
        placeholders: java.util.function.Function<String, String>
    ): List<String>
    {
        val list = getStringList(path, uuid)
        return list.stream()
            .map(placeholders::apply)
            .map(Utils::color)
            .toList()
    }

    private fun getLocales(): MutableMap<Locale, FileConfiguration>
    {
        val locales = mutableMapOf<Locale, FileConfiguration>()
        for ( file in getLocaleFiles() )
        {
            val locale = Utils.stringToLocale(file.nameWithoutExtension)
            locales[locale] = YamlConfiguration.loadConfiguration( file )
        }
        plugin.logger.info("Found ${locales.size} language files.")
        return locales
    }

    fun getAvailableLocales(): List<Locale>
    {
        return locales.keys.toList()
    }

    private fun getLocaleFiles(): List<File>
    {
        val files = ArrayList<File>()
        val directoryPath = File(plugin.dataFolder, "locales")
        val ymlFilter = FilenameFilter { _, name ->
            val lowercaseName = name.lowercase()
            return@FilenameFilter lowercaseName.endsWith(".yml")
        }
        val filesList = directoryPath.listFiles(ymlFilter)
        Objects.requireNonNull(filesList, "Locales folder not found!")
        Collections.addAll(files, *filesList!!)
        return files
    }

    private fun copyConfigFile(): FileConfiguration
    {
        if ( !plugin.dataFolder.exists() )
            plugin.dataFolder.mkdirs()

        file = File(plugin.dataFolder, "config.yml")

        if ( !file.exists() )
            plugin.saveDefaultConfig()

        return YamlConfiguration.loadConfiguration( file )
    }

    private fun copyLocalesFromFolder()
    {
        val filter = Predicate<Path> {
            val path = it.fileName.toString()
            return@Predicate path.endsWith(".yml")
        }
        FileUtils.getFilesIn("locales", filter)!!.forEach {
            val destination = File(plugin.dataFolder, "locales" + File.separator + it!!.name)
            if ( !destination.parentFile.exists() )
                destination.parentFile.mkdir()
            if ( !destination.exists() && !destination.isDirectory )
            {
                val fileString = "locales/${it.name}"
                plugin.stream( fileString ).use { `in` -> Files.copy( `in`!!, destination.toPath() ) }
            }
        }
    }

}