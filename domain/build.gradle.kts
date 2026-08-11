plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Must stay at 11 to match the Java 11 source/target of :app and :data. A higher toolchain emits
// class files those modules cannot inline.
kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
