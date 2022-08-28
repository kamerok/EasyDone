plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.edittask"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))
    implementation(project(":core-ui:design"))
    implementation(project(":feature:selecttype"))

    implementation(Android.accompanistAppCompat)
    implementation(Android.accompanistInsets)
    implementation(Android.accompanistInsetsUi)
    implementation(Android.activityCompose)
    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
}
