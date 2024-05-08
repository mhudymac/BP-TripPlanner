pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    plugins {
        id("org.kodein.mock.mockmp") version "1.17.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "TripPlanner"
include(":android:app", ":android:shared", ":shared")
include(":android:trip")
include(":android:gallery")
include(":android:search")
include(":android:home")
