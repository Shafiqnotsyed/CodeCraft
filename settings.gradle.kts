// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    // 🔽 Add explicit plugin versions for those NOT coming from libs.versions.toml
    plugins {
        id("com.android.application") version "8.6.1" apply false
        id("org.jetbrains.kotlin.android") version "2.0.21" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"

        // ✅ These two are required because you apply them by ID in app/build.gradle.kts
        id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"

        // Firebase Google Services plugin
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CodeCraft"
include(":app")