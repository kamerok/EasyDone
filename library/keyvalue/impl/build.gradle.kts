plugins {
    id("easydone.android.library")
}

android {
    namespace = "easydone.library.keyvalue.sharedprefs"
}

dependencies {
    implementation(project(":library:keyvalue"))

    implementation(Android.core)
    implementation(Android.dataStorePrefs)
}
