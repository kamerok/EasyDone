plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.kamer.selectboard"
}

dependencies {
    implementation(Android.appCompat)
    implementation(Android.core)
    implementation(Android.recyclerView)
}
