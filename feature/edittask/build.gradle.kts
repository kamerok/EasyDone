plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.feature.edittask"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)
    implementation(projects.feature.selecttype)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.tooling)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
