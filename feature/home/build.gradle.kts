plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "com.kamer.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.core.utils)
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.composeAnimation)
    implementation(Android.composeIcons)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
