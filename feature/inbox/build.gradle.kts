plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.inbox"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
