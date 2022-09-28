plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "com.kamer.selectboard"
}

dependencies {
    implementation(project(":core-ui:design"))

    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
}
