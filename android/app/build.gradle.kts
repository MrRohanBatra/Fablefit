import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rohan.fablefit"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.rohan.fablefit"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            val props =Properties()
            val propFile = rootProject.file("local.properties")
            if (propFile.exists()) {
                props.load(propFile.inputStream())

                // This looks for key.jks in the app folder
                storeFile = file(props.getProperty("RELEASE_STORE_FILE"))
                storePassword = props.getProperty("RELEASE_STORE_PASSWORD")
                keyAlias = props.getProperty("RELEASE_KEY_ALIAS")
                keyPassword = props.getProperty("RELEASE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true // Changed to true: essential for production/release
            signingConfig = signingConfigs.getByName("release") // This was missing!

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // Debug usually uses the default debug.keystore automatically
//            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // ---------------- CORE ----------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ---------------- COMPOSE ----------------
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)

//    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.runtime)

    debugImplementation(libs.androidx.compose.ui.tooling)

    // ---------------- NAVIGATION ----------------
    implementation(libs.androidx.navigation.compose)

    // ---------------- FIREBASE ----------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // ---------------- GOOGLE LOGIN (Credential Manager) ----------------
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.id)
    implementation(libs.androidx.credentials.v122)
    implementation(libs.androidx.credentials.play.services.auth.v122)
    implementation(libs.googleid.v111)
    // ---------------- TESTING ----------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    implementation(libs.glide.core)
//    implementation(libs.glide.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)


}