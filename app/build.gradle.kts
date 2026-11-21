
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val targetAbi = (project.findProperty("bl.abi") as String?)
    ?: "armeabi-v7a"

if (targetAbi !in listOf("armeabi-v7a", "arm64-v8a")) {
    throw GradleException("ABI não suportada: $targetAbi")
}

val nativeBuild = (project.findProperty("bl.nativeBuild") as String?)
    ?.toBoolean() ?: false

android {
    namespace = "dev.bunnyloader.test"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.bunnyloader.test"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"

        ndk {
            abiFilters.clear()
            abiFilters += targetAbi
        }

        if (nativeBuild) {
            externalNativeBuild {
                cmake {
                    cppFlags += "-std=c++20"
                }
            }
        }
    }

    if (nativeBuild) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.core:core-ktx:1.13.1")
}
