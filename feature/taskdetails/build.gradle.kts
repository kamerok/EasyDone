plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "easydone.feature.taskdetails"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)
    implementation(projects.feature.selecttype)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.compose.tooling)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)
    implementation(Libraries.markdown)
}
