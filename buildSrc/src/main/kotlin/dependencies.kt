@file:Suppress("MayBeConstant")

object Config {
    val minSdk = 21
    val compileSdk = 28
    val targetSdk = 28
}

object Versions {
    val androidx_appcompat = "1.1.0-rc01"
    val androidx_core = "1.2.0-alpha02"
    val androidx_recyclerview = "1.0.0"
    val androidx_constraintLayout = "1.1.3"
    val androidx_navigation = "2.1.0-alpha05"
    val material = "1.1.0-alpha08"

    val adapter_depegates = "4.1.1"
    val coroutines = "1.3.0-RC"
    val crashlytics = "2.10.1"
    val firebase = "17.0.1"
    val markwon = "4.0.2"
    val retrofit = "2.6.0"
    val retrofit_logging = "4.0.1"
    val rxjava = "2.2.10"
    val stetho = "1.5.1"
    val timber = "5.0.0-SNAPSHOT"

    val junit = "4.12"

    val kotlin = "1.3.41"
    val fabric = "1.30.0"
    val gradleandroid = "3.5.0-rc01"
    val gradleversions = "0.21.0"
    val sqldelight = "1.1.4"
    val google_services = "4.3.0"

}

object Plugins {
    val fabric = "io.fabric.tools:gradle:${Versions.fabric}"
    val google_services = "com.google.gms:google-services:${Versions.google_services}"
    val gradle_android = "com.android.tools.build:gradle:${Versions.gradleandroid}"
    val gradleversions = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleversions}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val safeArgs =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.androidx_navigation}"
    val sqldelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqldelight}"
}

object Deps {
    val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.androidx_appcompat}"
    val androidx_core = "androidx.core:core-ktx:${Versions.androidx_core}"
    val androidx_constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.androidx_constraintLayout}"
    val androidx_fragment = "androidx.fragment:fragment-ktx:${Versions.androidx_appcompat}"
    val androidx_material = "com.google.android.material:material:${Versions.material}"
    val androidx_navigation_fragment =
        "androidx.navigation:navigation-fragment-ktx:${Versions.androidx_navigation}"
    val androidx_navigation_ui =
        "androidx.navigation:navigation-ui-ktx:${Versions.androidx_navigation}"
    val androidx_recyclerview =
        "androidx.recyclerview:recyclerview:${Versions.androidx_recyclerview}"

    val kotilnStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val adapter_depelegates = "com.hannesdorfmann:adapterdelegates4:${Versions.adapter_depegates}"
    val adapter_depelegates_dsl =
        "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:${Versions.adapter_depegates}"
    val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutines_andorid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutines_rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"
    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    val firebase = "com.google.firebase:firebase-core:${Versions.firebase}"
    val markwon = "io.noties.markwon:core:${Versions.markwon}"
    val markwon_linkify = "io.noties.markwon:linkify:${Versions.markwon}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_logging = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofit_logging}"
    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    val sqldelight_android = "com.squareup.sqldelight:android-driver:${Versions.sqldelight}"
    val sqldelight_rxjava = "com.squareup.sqldelight:rxjava2-extensions:${Versions.sqldelight}"
    val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
    val timber = "com.jakewharton.timber:timber-android:${Versions.timber}"
    val timber_jdk = "com.jakewharton.timber:timber-jdk:${Versions.timber}"

    val junit = "junit:junit:${Versions.junit}"
}