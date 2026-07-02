@file:Suppress("UnstableApiUsage")

plugins {
    id("net.fabricmc.fabric-loom")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
    groovy
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["version"] = prop("mod.version")
        this["minecraft"] = prop("deps.minecraft")
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

tasks.named("processResources").configure { dependsOn("stonecutterGenerate") }
tasks.named("postProcessMainResources").configure { dependsOn("stonecutterGenerate") }

version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
base.archivesName = property("mod.id") as String

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${property("mod.id")}.accesswidener")
}

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

repositories {
    mavenLocal()
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
    }
    maven("https://api.modrinth.com/maven")
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    implementation("maven.modrinth:yacl:${property("deps.yacl")}")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.62")
    include("com.alibaba.fastjson2:fastjson2:2.0.62")

    val modules = listOf("transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
    for (it in modules) implementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))
}

fabricApi {
    configureDataGeneration {
        outputDirectory = file("$rootDir/src/main/generated")
        client = true
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar)
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = JavaVersion.VERSION_25

    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }

    type = STABLE
    displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Fabric"
    version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        environment=CLIENT_OR_SERVER
        if ("${property("deps.minecraft")} for ${stonecutter.current.version}".contains("snapshot")){
            minecraftVersions.add("${property("deps.minecraft")} for ${stonecutter.current.version}")
        } else {
            minecraftVersions.add(stonecutter.current.version)
        }
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
        requires("yacl")
    }
}