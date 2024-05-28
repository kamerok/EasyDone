plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.widget"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(libs.androidx.glance)
    implementation(libs.koin)
}
