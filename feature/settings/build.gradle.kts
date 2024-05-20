plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.settings"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.lifecycleRuntime)
}
