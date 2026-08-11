plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "id.my.hizari.dummyjsonpreview"
    // 37 is a floor, not a preference: core-ktx 1.19.0 and lifecycle 2.11.0 both refuse to compile
    // against anything lower.
    compileSdk = 37

    defaultConfig {
        applicationId = "id.my.hizari.dummyjsonpreview"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.coil.compose)

    // :domain exposes Flow on its public API through an `implementation` dependency, so declare
    // coroutines here rather than relying on it leaking in transitively.
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.core.ktx)

    testImplementation(libs.bundles.unit.test)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
