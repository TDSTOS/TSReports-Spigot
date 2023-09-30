package de.todesstoss.tsreports.util.file

import de.todesstoss.tsreports.TSReports
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate
import java.util.logging.Level
import java.util.stream.Collectors
import kotlin.collections.LinkedHashSet

object FileUtils {

    fun getFilesIn(
        path: String,
        filter: Predicate<in Path>
    ): Set<File?>?
    {
        var files: Set<File?>? = LinkedHashSet()
        val packagePath = path.replace(".", "/")
        try {
            val uri = TSReports::class.java.protectionDomain.codeSource.location.toURI()
            val fileSystem = FileSystems.newFileSystem(URI.create("jar:$uri"), emptyMap<String, Any>())
            files = Files.walk(fileSystem.getPath(packagePath))
                .filter(Objects::nonNull)
                .filter(filter)
                .map { File(it.toString()) }
                .collect(Collectors.toSet())
            fileSystem.close()
        } catch (ex: URISyntaxException) {
            TSReports.instance.logger
                .log(Level.SEVERE, "An error occurred while trying to load files: " + ex.message, ex)
        } catch (ex: IOException) {
            TSReports.instance.logger
                .log(Level.SEVERE, "An error occurred while trying to load files: " + ex.message, ex)
        }
        return files
    }

}