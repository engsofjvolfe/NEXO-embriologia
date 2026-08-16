plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.nexo.motor.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.nexo.motor.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
}
