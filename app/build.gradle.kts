// app/build.gradle.kts
import java.io.File
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.codecraft"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.codecraft"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // If you later want to expose a Gemini key via BuildConfig:
        val props = Properties().apply {
            val f = File(rootDir, "local.properties")
            if (f.exists()) f.inputStream().use(::load)
        }
        val apiKey = props.getProperty("GEMINI_API_KEY") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Compose + BuildConfig
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Compose compiler for Kotlin 2.x line
    composeOptions {
        // Works with Kotlin 2.0.x–2.2.x; stable for your codebase
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    // Java 17 + desugaring
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xskip-metadata-version-check"
        )
    }

    // Avoid META-INF clashes that sometimes appear with Google/Firebase/AI libs
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md,DEPENDENCIES}"
        }
    }
}

kotlin { jvmToolchain(17) }

dependencies {
    // ---- AndroidX via your Version Catalog ----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ---- Compose BOM + UI via catalog ----
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended) // catalog alias

    // Debug/tooling & tests
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // ---- Compose + ViewModel interop (not in catalog) ----
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // ---- Coroutines ----
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ---- Firebase via BOM (from your catalog) ----
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-firestore") // For user profile

    // ---- Room (KSP) ----
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ---- DataStore ----
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ---- WorkManager ----
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ---- Kotlin Serialization ----
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ---- Markdown ----
    implementation("io.noties.markwon:core:4.6.2")

    // ---- Gemini (0.9.0 as used by your provider) ----
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // ---- Desugaring ----
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ---- Tests via catalog ----
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

ksp {
    arg("room.schemaLocation", file("$projectDir/schemas").path)
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("room.skipQueryVerification", "true")
}
