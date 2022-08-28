plugins {
    id("easydone.android.library")
}

android {
    namespace = "com.kamer.selectboard"
}

dependencies {
    implementation(Android.appCompat)
    implementation(Android.core)
    implementation(Android.recyclerView)
}
