@file:Suppress("MayBeConstant")

object Config {
    val minSdk = 21
    val compileSdk = 28
    val targetSdk = 28
}

object Versions {
    val androidx_appcompat = "1.1.0-beta01"
    val androidx_core = "1.2.0-alpha01"
    val androidx_recyclerview = "1.0.0"
    val androidx_constraintLayout = "1.1.3"
    val androidx_navigation = "2.1.0-alpha05"
    val androidx_fragment = "1.1.0-beta01"
    val material = "1.1.0-alpha04"

    val coroutines = "1.1.1"
    val retrofit = "2.6.0"
    val retrofit_logging = "3.14.2"

    val junit = "4.12"

    val kotlin = "1.3.40"
    val gradleandroid = "3.5.0-beta04"
    val gradleversions = "0.21.0"

}

object Deps {
    val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.androidx_appcompat}"
    val androidx_core = "androidx.core:core-ktx:${Versions.androidx_core}"
    val androidx_constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.androidx_constraintLayout}"
    val androidx_fragment = "androidx.fragment:fragment-ktx:${Versions.androidx_fragment}"
    val androidx_material = "com.google.android.material:material:${Versions.material}"
    val androidx_navigation_fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.androidx_navigation}"
    val androidx_navigation_ui = "androidx.navigation:navigation-ui-ktx:${Versions.androidx_navigation}"
    val androidx_recyclerview = "androidx.recyclerview:recyclerview:${Versions.androidx_recyclerview}"

    val kotilnStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutines_andorid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_logging = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofit_logging}"

    val junit = "junit:junit:${Versions.junit}"

    val gradle_android = "com.android.tools.build:gradle:${Versions.gradleandroid}"
    val gradleversions = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleversions}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.androidx_navigation}"

}