plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.kamer.setupflow"
}

dependencies {
    implementation(project(":core:service:trello"))
    implementation(project(":library:navigation"))
    implementation(project(":feature:login"))
    implementation(project(":feature:selectboard"))

    implementation(Android.appCompat)
    implementation(Android.core)
    implementation(Android.fragment)
    implementation(Android.lifecycleRuntime)
}