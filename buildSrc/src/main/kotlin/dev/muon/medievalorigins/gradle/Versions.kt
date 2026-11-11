package dev.muon.medievalorigins.gradle

object Versions {
    const val MOD = "8.0.0"

    const val MINECRAFT = "1.21.1"
    const val NEOFORM = "$MINECRAFT-20240808.144430"
    const val PARCHMENT_MINECRAFT = "1.21.1"
    const val PARCHMENT = "2024.11.17"
    const val JAVA = 21

    const val FABRIC_API = "0.116.18+1.21.1"
    const val FABRIC_LOADER = "0.20.1"
    const val FABRIC_LOADER_RANGE = ">=0.20.1"
    const val FABRIC_MINECRAFT_RANGE = "~1.21.1"

    const val NEOFORGE = "21.1.265"
    const val NEOFORGE_LOADER_RANGE = "[4,)"
    const val NEOFORGE_MINECRAFT_RANGE = "[1.21.1,1.22)"

    const val FABRIC_MIXIN = "0.17.5+mixin.0.8.7"
    const val MIXIN_EXTRAS = "0.5.5"

    // Overgrown's Apoli and Origins, as Modrinth version ids. Fabric and NeoForge
    // builds share version numbers, so a bare number can resolve to the wrong loader.
    const val APOLI_DISPLAY = "1.129.0"
    const val APOLI_FABRIC = "9tnOiYZD"
    const val APOLI_NEOFORGE = "ivct5ecf"
    const val ORIGINS_DISPLAY = "1.52.0"
    const val ORIGINS_FABRIC = "Osv0g1oE"
    const val ORIGINS_NEOFORGE = "dFrPRXFk"

    const val ADDITIONAL_ENTITY_ATTRIBUTES = "2.0.2+1.21.1"

    const val SPELL_ENGINE = "1.10.9+1.21.1"
    const val SPELL_POWER = "1.6.0+1.21.1"
    const val PLAYER_ANIMATOR = "2.0.4+1.21.1"
    const val CLOTH_CONFIG = "15.0.140"

    // Icarus and its dependencies, as Modrinth version ids (each shares a version number across loaders)
    const val ICARUS_FABRIC = "X6b0raef"
    const val ICARUS_NEOFORGE = "WYDi2tl4"
    const val RESOURCEFUL_CONFIG_FABRIC = "dQh99ERC"
    const val RESOURCEFUL_CONFIG_NEOFORGE = "lSbyRD6v"
    const val COMMON_NETWORK_FABRIC = "qJXqSlRN"
    const val COMMON_NETWORK_NEOFORGE = "pR7AeZk3"
    const val SPARKWEAVE_FABRIC = "UQuUW6MR"
    const val SPARKWEAVE_NEOFORGE = "j8reYKe3"
    const val TRINKETS = "3.10.0"
    const val CURIOS = "9.5.1+1.21.1"
    const val CAELUS = "7.0.1+1.21.1"

    const val FTB_LIBRARY = "2101.1.39"
    const val FTB_TEAMS = "2101.1.12"
    const val ARCHITECTURY = "13.0.11"
}
