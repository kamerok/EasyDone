plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "easydone.feature.taskdetails"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:strings"))
    implementation(project(":core-ui:design"))
    implementation(project(":feature:selecttype"))

    implementation(Android.accompanistInsets)
    implementation(Android.activityCompose)
    implementation(Android.appCompat)
    implementation(Android.composeTooling)
    implementation(Android.core)
    implementation(Android.fragment)
    implementation(Libraries.markdown)
}