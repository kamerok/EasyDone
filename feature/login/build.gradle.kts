plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "com.kamer.login"
}

dependencies {
    implementation(projects.core.service.trello)
    implementation(projects.coreUi.design)

    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.compose.tooling)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines)
}
