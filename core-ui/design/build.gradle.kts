plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.coreui.design"
}

dependencies {
    api(libs.androidx.compose.material)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.tooling)
}
