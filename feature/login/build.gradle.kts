plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.kamer.login"
}

dependencies {
    implementation(project(":core:service:trello"))

    implementation(Android.appCompat)
    implementation(Android.browser)
    implementation(Android.constraintLayout)
    implementation(Android.core)
    implementation(Android.lifecycleRuntime)
    implementation(Kotlin.coroutinesAndorid)
    implementation(Kotlin.coroutinesCore)
}
