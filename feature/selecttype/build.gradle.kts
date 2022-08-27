plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.feature.selecttype"

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
    implementation(project(":core-ui:design"))

    implementation(Android.composeTooling)
}
