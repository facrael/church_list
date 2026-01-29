pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // TODO: 2GIS SDK repository (добавить когда будет нужна карта)
        // maven { url = uri("https://artifactory.2gis.dev/sdk-maven-release") }
    }
}

rootProject.name = "ReligiousMarket"
include(":app")
