import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // === ПР 11 — Firebase ===
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Данные подписи: локально из keystore.properties, на сервере (CI) из переменных окружения
val keystoreProps = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val keystoreFile = file("keystore.jks")
val storePasswordValue: String? = keystoreProps.getProperty("storePassword") ?: System.getenv("KEYSTORE_PASSWORD")
val keyAliasValue: String? = keystoreProps.getProperty("keyAlias") ?: System.getenv("KEY_ALIAS")
val keyPasswordValue: String? = keystoreProps.getProperty("keyPassword") ?: System.getenv("KEY_PASSWORD")
val canSignRelease = keystoreFile.exists() &&
        storePasswordValue != null && keyAliasValue != null && keyPasswordValue != null

android {
    namespace = "com.example.pr10"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pr10"
        minSdk = 24
        targetSdk = 37
        versionCode = 42
        versionName = "2.1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (canSignRelease) {
            create("release") {
                storeFile = keystoreFile
                storePassword = storePasswordValue
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            // === ПР 11: включаем R8 обфускацию/минификацию ===
            // (раньше было enable = false)
            optimization {
                enable = true
            }
            if (canSignRelease) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        // === ПР 11: BuildConfig для Remote Config ===
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // === ПР 11 — Firebase ===
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)        // Remote Config (п. 3.2)
    implementation(libs.firebase.crashlytics)   // Crashlytics (п. 3.1 — alert 50%)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}