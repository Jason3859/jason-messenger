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

include(":messenger-server")
include(":messenger-android")
include(":messenger-android:app")
include(":messenger-android:auth")
include(":messenger-android:auth:auth-data")
include(":messenger-android:auth:auth-domain")
include(":messenger-android:auth:auth-ui")
