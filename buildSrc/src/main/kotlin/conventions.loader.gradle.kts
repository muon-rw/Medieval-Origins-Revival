import dev.muon.medievalorigins.gradle.Properties

plugins {
    id("conventions.common")
}

configurations {
    register("commonJava") {
        isCanBeResolved = true
    }
    register("commonResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("$group:${Properties.MOD_ID}")
        }
    }
    "commonJava"(project(":common", "commonJava"))
    "commonResources"(project(":common", "commonResources"))
}

tasks {
    named<JavaCompile>("compileJava").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<ProcessResources>("processResources").configure {
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
    named<Javadoc>("javadoc").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<Jar>("sourcesJar").configure {
        dependsOn(configurations.getByName("commonJava"))
        from(configurations.getByName("commonJava"))
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
}
