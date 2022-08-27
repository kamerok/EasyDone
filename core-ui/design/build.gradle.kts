plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.coreui.design"

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    api(Android.composeMaterial)

    implementation(Android.activityCompose)
    implementation(Android.composeIcons)
    implementation(Android.composeTooling)
}
