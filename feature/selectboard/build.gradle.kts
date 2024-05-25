plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "com.kamer.selectboard"
}

dependencies {
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
}
