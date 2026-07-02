pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    create(rootProject) {
        fun match(versions: String, vararg loaders: String) = loaders
            .forEach { version("$versions-$it", versions).buildscript = "build.$it.gradle.kts" }

        /*match("1.21.11", "fabric", "neoforge")
        match("1.21.9", "fabric", "neoforge")
        match("1.21.6", "fabric", "neoforge")
        match("1.21.4", "fabric", "neoforge")
        match("1.21.2", "fabric", "neoforge")*/
        match("1.21.5", "fabric", "neoforge")
        match("1.21.1", "fabric", "neoforge")
        match("1.20.1", "fabric")


        vcsVersion = "1.21.1-fabric"
    }
}
