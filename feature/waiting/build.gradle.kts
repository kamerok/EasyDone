plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.waiting"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(Android.accompanistInsets)
    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
