plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.coreui.design"
}

dependencies {
    api(Android.composeMaterial)

    implementation(Android.activityCompose)
    implementation(Android.composeIcons)
    implementation(Android.composeTooling)
}
