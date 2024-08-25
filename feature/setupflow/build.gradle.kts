plugins {
    alias(libs.plugins.easydone.android.library.compose)
}

android {
    namespace = "com.kamer.setupflow"
}

dependencies {
    implementation(projects.core.service.trello)
    implementation(projects.coreUi.design)
    implementation(projects.feature.login)
    implementation(projects.feature.selectboard)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
