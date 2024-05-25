plugins {
    alias(libs.plugins.easydone.android.library)
}

android {
    namespace = "easydone.library.navigation"
}

dependencies {
    implementation(Android.fragment)
}
