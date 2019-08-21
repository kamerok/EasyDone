@file:Suppress("MayBeConstant")

object Config {
    val compileSdk = 28
    val minSdk = 28
    val targetSdk = 28
}

object Versions {
    val adapterDelegates = "4.1.1"
    val appCompat = "1.1.0-rc01"
    val assertk = "0.19"
    val constraintLayout = "1.1.3"
    val core = "1.2.0-alpha02"
    val coroutines = "1.3.0-RC"
    val crashlytics = "2.10.1"
    val fabric = "1.31.0"
    val firebase = "17.1.0"
    val googleServices = "4.3.0"
    val gradleAndroid = "3.5.0"
    val gradleVersions = "0.22.0"
    val junit = "4.12"
    val kotlin = "1.3.41"
    val lifecycle = "2.2.0-alpha01"
    val markwon = "4.1.0"
    val material = "1.1.0-alpha08"
    val recyclerView = "1.0.0"
    val retrofit = "2.6.1"
    val retrofitLogging = "4.1.0"
    val rxJava = "2.2.11"
    val sqlDelight = "1.1.4"
    val stetho = "1.5.1"
    val timber = "5.0.0-SNAPSHOT"
}

object Plugins {
    val fabric = "io.fabric.tools:gradle:${Versions.fabric}"
    val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
    val gradleAndroid = "com.android.tools.build:gradle:${Versions.gradleAndroid}"
    val gradleVersions = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersions}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
}

object Android {
    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val core = "androidx.core:core-ktx:${Versions.core}"
    val fragment = "androidx.fragment:fragment-ktx:${Versions.appCompat}"
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    val material = "com.google.android.material:material:${Versions.material}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
}

object Kotlin {
    val coroutinesAndorid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesRx = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"
    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
}

object Tools {
    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    val firebase = "com.google.firebase:firebase-core:${Versions.firebase}"
    val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
}

object Libraries {
    val adapterDepelegates = "com.hannesdorfmann:adapterdelegates4:${Versions.adapterDelegates}"
    val adapterDepelegatesDsl = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:${Versions.adapterDelegates}"
    val markwon = "io.noties.markwon:core:${Versions.markwon}"
    val markwonLinkify = "io.noties.markwon:linkify:${Versions.markwon}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofitLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLogging}"
    val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    val sqlDelightAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    val sqlDelightRxJava = "com.squareup.sqldelight:rxjava2-extensions:${Versions.sqlDelight}"
    val timberAndroid = "com.jakewharton.timber:timber-android:${Versions.timber}"
    val timberJdk = "com.jakewharton.timber:timber-jdk:${Versions.timber}"
    val timber = arrayOf(timberAndroid, timberJdk)
}

object Tests {
    val assertk = "com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}"
    val junit = "junit:junit:${Versions.junit}"
}
