plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.inbox"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))
    implementation(project(":core-ui:design"))

    implementation(Android.accompanistInsets)
    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
