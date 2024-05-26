plugins {
    alias(libs.plugins.easydone.android.library)
}

android {
    namespace = "com.kamer.setupflow"
}

dependencies {
    implementation(projects.core.service.trello)
    implementation(projects.library.navigation)
    implementation(projects.feature.login)
    implementation(projects.feature.selectboard)

    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
