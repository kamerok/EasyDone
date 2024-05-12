plugins {
    id("easydone.android.library")
}

android {
    namespace = "com.kamer.setupflow"
}

dependencies {
    implementation(projects.core.service.trello)
    implementation(projects.library.navigation)
    implementation(projects.feature.login)
    implementation(projects.feature.selectboard)

    implementation(Android.appCompat)
    implementation(Android.core)
    implementation(Android.fragment)
    implementation(Android.lifecycleRuntime)
}
