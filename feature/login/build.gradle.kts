plugins {
    id("easydone.android.library")
}

android {
    namespace = "com.kamer.login"
}

dependencies {
    implementation(projects.core.service.trello)

    implementation(Android.appCompat)
    implementation(Android.browser)
    implementation(Android.constraintLayout)
    implementation(Android.core)
    implementation(Android.lifecycleRuntime)
    implementation(Kotlin.coroutinesAndorid)
    implementation(Kotlin.coroutinesCore)
}
