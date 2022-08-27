plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.library.navigation"
}

dependencies {
    implementation(Android.fragment)
}
