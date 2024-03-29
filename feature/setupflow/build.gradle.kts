plugins {
    id("easydone.android.library")
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
