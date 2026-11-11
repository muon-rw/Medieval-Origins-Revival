import dev.muon.medievalorigins.gradle.Properties
import dev.muon.medievalorigins.gradle.Versions

plugins {
    base
    `java-library`
    idea
    `maven-publish`
}

base.archivesName.set("${Properties.ARCHIVES_NAME}-${project.name}")
group = Properties.GROUP
version = "${Versions.MOD}+${Versions.MINECRAFT}"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA))
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") {
                name = "Sponge"
            }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
            maven("https://maven.parchmentmc.org/") {
                name = "ParchmentMC"
            },
            maven("https://maven.neoforged.net/releases") {
                name = "NeoForge"
            }
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com") {
                name = "CurseMaven"
            }
        }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.ftb.dev/releases") {
                name = "FTB"
            }
        }
        filter {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.ftb.mods")
        }
    }
    maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
        content {
            includeGroupAndSubgroups("me.shedaniel")
            includeGroup("dev.architectury")
        }
    }
    maven("https://maven.kosmx.dev/") {
        name = "KosmX"
        content { includeGroupAndSubgroups("dev.kosmx") }
    }
    maven("https://maven.theillusivec4.top/") {
        name = "Illusive"
        content {
            includeGroupAndSubgroups("top.theillusivec4")
            includeGroupAndSubgroups("com.illusivesoulworks")
        }
    }
    maven("https://maven.ladysnake.org/releases") {
        name = "Ladysnake"
        content {
            includeGroupAndSubgroups("dev.emi")
            includeGroupAndSubgroups("org.ladysnake")
        }
    }
    maven("https://maven.terraformersmc.com/") {
        name = "TerraformersMC"
        content {
            includeGroupAndSubgroups("dev.emi")
            includeGroupAndSubgroups("com.terraformersmc")
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
}

// Declare capabilities on the outgoing configurations.
// Read more about capabilities here: https://docs.gradle.org/current/userguide/component_capabilities.html#sec:declaring-additional-capabilities-for-a-local-component
setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.getByName(variant).outgoing {
        capability("$group:${Properties.MOD_ID}-${project.name}:$version")
        capability("$group:${Properties.MOD_ID}:$version")
    }
    publishing.publications.forEach { publication ->
        if (publication is MavenPublication) {
            publication.suppressPomMetadataWarningsFor(variant)
        }
    }
}

tasks {
    named<Jar>("sourcesJar").configure {
        from(rootProject.file("LICENSE.md")) {
            rename { "LICENSE_${Properties.MOD_ID}.md" }
        }
    }
    named<Jar>("jar").configure {
        from(rootProject.file("LICENSE.md")) {
            rename { "LICENSE_${Properties.MOD_ID}.md" }
        }

        manifest {
            attributes["Specification-Title"] = Properties.MOD_NAME
            attributes["Specification-Vendor"] = Properties.MOD_AUTHOR
            attributes["Specification-Version"] = archiveVersion
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = archiveVersion
            attributes["Implementation-Vendor"] = Properties.MOD_AUTHOR
            attributes["Built-On-Minecraft"] = Versions.MINECRAFT
        }
    }

    named<Javadoc>("javadoc").configure {
        isFailOnError = false
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    val expandProps = mapOf(
        "mod_version" to Versions.MOD,
        "group" to project.group, // Else we target the task's group.
        "minecraft_version" to Versions.MINECRAFT,
        "fabric_loader_range" to Versions.FABRIC_LOADER_RANGE,
        "fabric_minecraft_version_range" to Versions.FABRIC_MINECRAFT_RANGE,
        "mod_name" to Properties.MOD_NAME,
        "mod_author" to Properties.MOD_AUTHOR,
        "neoforge_mod_contributors" to Properties.MOD_CONTRIBUTORS.joinToString(),
        "fabric_mod_contributors" to Properties.MOD_CONTRIBUTORS.joinToString(separator = "\",\n\t\t\""),
        "mod_id" to Properties.MOD_ID,
        "mod_license" to Properties.LICENSE,
        "mod_description" to Properties.DESCRIPTION,
        "neoforge_minecraft_version_range" to Versions.NEOFORGE_MINECRAFT_RANGE,
        "neoforge_loader_version_range" to Versions.NEOFORGE_LOADER_RANGE,
        "java_version" to Versions.JAVA,
        "apoli_version" to Versions.APOLI_DISPLAY,
        "origins_version" to Versions.ORIGINS_DISPLAY,
        "curseforge_page" to Properties.CURSEFORGE_PAGE,
        "modrinth_page" to Properties.MODRINTH_PAGE,
        "sources" to Properties.GITHUB_REPO
    )

    val processResourcesTasks = listOf("processResources", "processTestResources")

    withType<ProcessResources>().matching { processResourcesTasks.contains(it.name) }.configureEach {
        inputs.properties(expandProps)
        filesMatching(setOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
            expand(expandProps)
        }
        exclude("\\.cache")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "${Properties.MOD_ID}-${project.name}"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "Muon"
            url = uri(System.getenv("MAVEN_URL") ?: "https://maven.muon.rip/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

plugins.withId("me.modmuss50.mod-publish-plugin") {
    tasks.named("publishMods") {
        dependsOn("publishAllPublicationsToMuonRepository")
    }
}

idea {
    module {
        isDownloadSources = true
    }
}
