plugins {
    id("easydone.android.library.compose")
}

android {
    namespace = "easydone.widget"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))

    implementation(Android.glance)
    implementation(Libraries.koin)
}
