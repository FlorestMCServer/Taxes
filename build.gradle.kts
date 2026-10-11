plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "ru.florestdev"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.FlorestMCServer:PassportManager:3.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        filteringCharset = "UTF-8"
        // аналог <filtering>true</filtering> — подставляет ${...} из gradle.properties / version и т.п.
        filesMatching("**/*") {
            // при необходимости укажи конкретные файлы, например "plugin.yml"
        }
    }

    shadowJar {
        archiveClassifier.set("") // чтобы jar назывался Taxes-1.0.jar, а не -all
    }

    build {
        dependsOn(shadowJar)
    }
}