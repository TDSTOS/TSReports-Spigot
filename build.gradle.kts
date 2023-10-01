import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.cloudnetservice.eu/repository/snapshots/")
    mavenCentral()
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.0.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.1.214")
    implementation(platform("eu.cloudnetservice.cloudnet:bom:4.0.0-RC8"))
    compileOnly("eu.cloudnetservice.cloudnet:driver")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("net.luckperms:api:5.4")
}

group = "de.todesstoss"
version = "1.0.0"
description = "TSReports"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("com.zaxxer.hikari", "de.todesstoss.libraries.hikari")
        relocate("org.h2", "de.todesstoss.libraries.h2")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    name = "TSReports"
    main = "de.todesstoss.tsreports.TSReports"
    description = "Extensive Report Plugin"
    version = project.version.toString()
    author = "Todesstoss"
    softDepend = listOf("LuckPerms")

    permissions {

        register("tsreports.admin") {
            children = listOf(
                "tsreports.bypass.*",
                "tsreports.delete",
                "tsreports.autologin",
                "tsreports.staff",
                "tsreports.warnings",
                "tsreports.notify"
            ) }

        register("tsreports.bypass.*") {
            children = listOf(
                "tsreports.bypass.in_process",
                "tsreports.bypass.report"
            ) }

        // Default
        register("tsreports.delete")
        register("tsreports.autologin")
        register("tsreports.staff")
        register("tsreports.warnings")
        register("tsreports.notify")

        // Bypass
        register("tsreports.bypass.in_process")
        register("tsreports.bypass.report")

    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
