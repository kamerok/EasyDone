plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.edittask"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)
    implementation(projects.feature.selecttype)

    implementation(Android.activityCompose)
    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
