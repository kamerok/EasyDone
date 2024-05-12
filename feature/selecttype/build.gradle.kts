plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.feature.selecttype"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(Android.composeTooling)
}
