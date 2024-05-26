plugins {
    alias(libs.plugins.easydone.android.library)
}

android {
    namespace = "easydone.library.keyvalue.sharedprefs"
}

dependencies {
    implementation(projects.library.keyvalue)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.prefs)
}
