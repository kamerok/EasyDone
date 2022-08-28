plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "com.kamer.home"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))
    implementation(project(":core:utils"))
    implementation(project(":core-ui:design"))

    implementation(Android.accompanistInsets)
    implementation(Android.appCompat)
    implementation(Android.composeAnimation)
    implementation(Android.composeIcons)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
