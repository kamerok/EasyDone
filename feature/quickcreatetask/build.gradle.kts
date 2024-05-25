plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.feature.quickcreatetask"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.lifecycleRuntime)
    implementation(Kotlin.coroutinesCore)
}
