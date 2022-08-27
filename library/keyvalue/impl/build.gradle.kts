plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.library.keyvalue.sharedprefs"
}

dependencies {
    implementation(project(":library:keyvalue"))

    implementation(Android.core)
    implementation(Android.dataStorePrefs)
}
