import dev.muon.medievalorigins.gradle.Properties
import dev.muon.medievalorigins.gradle.Versions
import me.modmuss50.mpp.PublishModTask
import org.gradle.jvm.tasks.Jar

plugins {
    id("conventions.loader")
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.mixinmcp.decompile")
}

// Loom strips jar-in-jar from mods it remaps, and Modrinth's Maven POMs declare no dependencies,
// so nested libraries of Modrinth mods never reach the dev runtime unless we extract them here.
val modrinthJarInJarHosts: Configuration by configurations.creating { isTransitive = false }

dependencies {
    minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${Versions.PARCHMENT_MINECRAFT}:${Versions.PARCHMENT}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")

    // Origins
    modImplementation("maven.modrinth:opoli:${Versions.APOLI_FABRIC}")
    modImplementation("maven.modrinth:overgrowns-origins:${Versions.ORIGINS_FABRIC}")

    modImplementation(include("maven.modrinth:AdditionalEntityAttributes:${Versions.ADDITIONAL_ENTITY_ATTRIBUTES}")!!)

    // Spell Engine
    modImplementation("maven.modrinth:spell-power:${Versions.SPELL_POWER}-fabric")
    modImplementation("maven.modrinth:spell-engine:${Versions.SPELL_ENGINE}-fabric")
    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${Versions.PLAYER_ANIMATOR}")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${Versions.CLOTH_CONFIG}")

    // Icarus
    modImplementation("maven.modrinth:icarus:${Versions.ICARUS_FABRIC}")
    modImplementation("dev.emi:trinkets:${Versions.TRINKETS}")
    modLocalRuntime("maven.modrinth:resourceful-config:${Versions.RESOURCEFUL_CONFIG_FABRIC}")
    modLocalRuntime("maven.modrinth:common-network:${Versions.COMMON_NETWORK_FABRIC}")
    modLocalRuntime("maven.modrinth:sparkweave:${Versions.SPARKWEAVE_FABRIC}")

    // Spell Engine nests the same Tiny Config as Spell Power; extracting both would load a duplicate mod id.
    modrinthJarInJarHosts("maven.modrinth:spell-power:${Versions.SPELL_POWER}-fabric")
    modrinthJarInJarHosts("maven.modrinth:sparkweave:${Versions.SPARKWEAVE_FABRIC}")
    modLocalRuntime(files(provider {
        modrinthJarInJarHosts.flatMap { zipTree(it).matching { include("META-INF/jars/*.jar") }.files }
    }))

    // FTB. Library's POM pulls in JEI and Amecs from mavens we don't need, so we resolve it non-transitively.
    modImplementation("dev.ftb.mods:ftb-library-fabric:${Versions.FTB_LIBRARY}") { isTransitive = false }
    modImplementation("dev.ftb.mods:ftb-teams-fabric:${Versions.FTB_TEAMS}") { isTransitive = false }
    modImplementation("dev.architectury:architectury-fabric:${Versions.ARCHITECTURY}") { isTransitive = false }
    modImplementation("curse.maven:teams-friendly-fire-addon-1477515:8545148")

    modCompileOnly("curse.maven:iceandfire-ce-1040076:7722413")
    modCompileOnly("curse.maven:uranus-1010827:8587523")
    modCompileOnly("curse.maven:jupiter-1072905:7738309")

    // Dev env
    modLocalRuntime("maven.modrinth:fabric-permissions-api:0.3.1")
}

loom {
    mods {
        register(Properties.MOD_ID) {
            sourceSet(sourceSets["main"])
        }
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

publishMods {
    file.set(tasks.named<Jar>("remapJar").get().archiveFile)
    modLoaders.add("fabric")
    changelog = rootProject.file("CHANGELOG.md").readText()
    displayName = "Fabric-${Versions.MOD}+${Versions.MINECRAFT}"
    version = "${Versions.MOD}+${Versions.MINECRAFT}-fabric"
    type = BETA

    modrinth {
        projectId = Properties.MODRINTH_PROJECT_ID
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(Versions.MINECRAFT)
        requires("opoli", "overgrowns-origins", "spell-engine")
    }

    curseforge {
        projectId = Properties.CURSEFORGE_PROJECT_ID
        accessToken = providers.environmentVariable("CF_TOKEN")
        minecraftVersions.add(Versions.MINECRAFT)
        javaVersions.add(JavaVersion.VERSION_21)
        client = true
        server = true
        requires("apoli", "o-origins", "spell-engine")
    }
}

tasks.withType<PublishModTask>().configureEach {
    mustRunAfter(rootProject.subprojects.map { "${it.path}:publishAllPublicationsToMuonRepository" })
}
