enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Messenger"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include("messenger-server")
include(":messenger-android")
include(":messenger-desktop")
include(":auth")
