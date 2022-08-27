plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.widget"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))

    implementation(Android.glance)
    implementation(Libraries.koin)
}
