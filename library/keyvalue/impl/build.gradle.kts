plugins {
    id("easydone.android.library")
}

android {
    namespace = "easydone.library.keyvalue.sharedprefs"
}

dependencies {
    implementation(projects.library.keyvalue)

    implementation(Android.core)
    implementation(Android.dataStorePrefs)
}
