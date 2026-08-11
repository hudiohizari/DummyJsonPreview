plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "id.my.hizari.dummyjsonpreview.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    // No buildTypes block: shrinking happens in :app, and AGP 9 dropped isMinifyEnabled from the
    // public library BuildType DSL.

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.bundles.network)
    // Debug builds get the in-app HTTP inspector; release swaps in the no-op so nothing ships.
    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.chucker.library.no.op)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.bundles.unit.test)
}
