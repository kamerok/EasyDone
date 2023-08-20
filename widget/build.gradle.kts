plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.widget"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))
    implementation(project(":core-ui:design"))

    implementation(Android.glance)
    implementation(Libraries.koin)
}
