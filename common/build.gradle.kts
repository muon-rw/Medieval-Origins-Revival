import dev.muon.medievalorigins.gradle.Properties
import dev.muon.medievalorigins.gradle.Versions

plugins {
    id("conventions.common")
    id("net.neoforged.moddev")
    id("dev.mixinmcp.decompile")
}

neoForge {
    neoFormVersion = Versions.NEOFORM
    parchment {
        minecraftVersion = Versions.PARCHMENT_MINECRAFT
        mappingsVersion = Versions.PARCHMENT
    }

    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists())
        setAccessTransformers(at)
    validateAccessTransformers = true
}

// Common compiles against the NeoForge builds of our dependencies because they ship in Mojang
// mappings. Each loader recompiles this source set against its own builds, so code here may only
// use API that both loaders' builds share.
dependencies {
    compileOnly("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
    compileOnly("net.fabricmc:sponge-mixin:${Versions.FABRIC_MIXIN}")

    compileOnly("maven.modrinth:opoli:${Versions.APOLI_NEOFORGE}")
    compileOnly("maven.modrinth:overgrowns-origins:${Versions.ORIGINS_NEOFORGE}")

    compileOnly("maven.modrinth:spell-power:${Versions.SPELL_POWER}-neoforge")
    compileOnly("maven.modrinth:spell-engine:${Versions.SPELL_ENGINE}-neoforge")

    compileOnly("maven.modrinth:icarus:${Versions.ICARUS_NEOFORGE}")

    compileOnly("dev.ftb.mods:ftb-teams-neoforge:${Versions.FTB_TEAMS}") { isTransitive = false }
    compileOnly("dev.ftb.mods:ftb-library-neoforge:${Versions.FTB_LIBRARY}") { isTransitive = false }
    compileOnly("curse.maven:teams-friendly-fire-addon-1477515:8545149")

    compileOnly("curse.maven:iceandfire-ce-1040076:9455119")
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets["main"].java.sourceDirectories.singleFile)
    add("commonResources", sourceSets["main"].resources.sourceDirectories.singleFile)
}
