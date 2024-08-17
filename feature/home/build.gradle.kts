plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "com.kamer.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.core.utils)
    implementation(projects.coreUi.design)

    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.tooling)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
