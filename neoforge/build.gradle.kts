import dev.muon.medievalorigins.gradle.Properties
import dev.muon.medievalorigins.gradle.Versions
import me.modmuss50.mpp.PublishModTask
import org.apache.tools.ant.filters.LineContains
import org.gradle.jvm.tasks.Jar

plugins {
    id("conventions.loader")
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.mixinmcp.decompile")
}

// Loom provides `localRuntime` out of the box; MDG does not, so we wire it up here.
val localRuntime: Configuration by configurations.creating
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

dependencies {
    // Origins
    implementation("maven.modrinth:opoli:${Versions.APOLI_NEOFORGE}")
    implementation("maven.modrinth:overgrowns-origins:${Versions.ORIGINS_NEOFORGE}")

    implementation(jarJar("maven.modrinth:AdditionalEntityAttributes:${Versions.ADDITIONAL_ENTITY_ATTRIBUTES}-neoforge")!!)

    // Spell Engine
    implementation("maven.modrinth:spell-power:${Versions.SPELL_POWER}-neoforge")
    implementation("maven.modrinth:spell-engine:${Versions.SPELL_ENGINE}-neoforge")
    implementation("dev.kosmx.player-anim:player-animation-lib-forge:${Versions.PLAYER_ANIMATOR}")
    implementation("me.shedaniel.cloth:cloth-config-neoforge:${Versions.CLOTH_CONFIG}")
    implementation("top.theillusivec4.curios:curios-neoforge:${Versions.CURIOS}")

    // Icarus
    implementation("maven.modrinth:icarus:${Versions.ICARUS_NEOFORGE}")
    localRuntime("com.illusivesoulworks.caelus:caelus-neoforge:${Versions.CAELUS}")
    localRuntime("maven.modrinth:resourceful-config:${Versions.RESOURCEFUL_CONFIG_NEOFORGE}")
    localRuntime("maven.modrinth:common-network:${Versions.COMMON_NETWORK_NEOFORGE}")
    localRuntime("maven.modrinth:sparkweave:${Versions.SPARKWEAVE_NEOFORGE}")

    // FTB
    implementation("dev.ftb.mods:ftb-library-neoforge:${Versions.FTB_LIBRARY}") { isTransitive = false }
    implementation("dev.ftb.mods:ftb-teams-neoforge:${Versions.FTB_TEAMS}") { isTransitive = false }
    implementation("dev.architectury:architectury-neoforge:${Versions.ARCHITECTURY}") { isTransitive = false }
    implementation("curse.maven:teams-friendly-fire-addon-1477515:8545149")

    compileOnly("curse.maven:iceandfire-ce-1040076:9455119")
}

neoForge {
    version = Versions.NEOFORGE
    parchment {
        minecraftVersion = Versions.PARCHMENT_MINECRAFT
        mappingsVersion = Versions.PARCHMENT
    }

    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists())
        setAccessTransformers(at)
    validateAccessTransformers = true

    runs {
        create("client") {
            client()
            ideName = "NeoForge Client"
            gameDirectory.set(file("runs/client"))
        }
        create("server") {
            server()
            ideName = "NeoForge Server"
            gameDirectory.set(file("runs/server"))
            programArgument("--nogui")
        }
    }

    mods {
        register(Properties.MOD_ID) {
            sourceSet(sourceSets["main"])
        }
    }
}

tasks {
    named<ProcessResources>("processResources").configure {
        filesMatching("*.mixins.json") {
            filter<LineContains>("negate" to true, "contains" to setOf("refmap"))
        }
    }
}

publishMods {
    file.set(tasks.named<Jar>("jar").get().archiveFile)
    modLoaders.add("neoforge")
    changelog = rootProject.file("CHANGELOG.md").readText()
    displayName = "NeoForge-${Versions.MOD}+${Versions.MINECRAFT}"
    version = "${Versions.MOD}+${Versions.MINECRAFT}-neoforge"
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
