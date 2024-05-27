plugins {
    alias(libs.plugins.easydone.android.library)
}

android {
    namespace = "com.kamer.login"
}

dependencies {
    implementation(projects.core.service.trello)

    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.constraint.layout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines)
}
