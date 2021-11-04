@file:Suppress("MayBeConstant")

object Config {
    val compileSdk = 31
    val minSdk = 28
    val targetSdk = 31
}

object Versions {
    val accompanist = "0.21.0-beta"
    val activityCompose = "1.3.1"
    val adapterDelegates = "4.2.0"
    val appCompat = "1.1.0"
    val assertk = "0.19"
    val browser = "1.0.0"
    val compose = "1.0.5"
    val constraintLayout = "1.1.3"
    val core = "1.6.0"
    val coroutines = "1.5.2"
    val dataStore = "1.0.0"
    val desugar = "1.1.5"
    val firebase = "17.2.1"
    val flipper = "0.117.0"
    val fragment = "1.3.6"
    val googleServices = "4.3.2"
    val gradleAndroid = "7.0.2"
    val gradleVersions = "0.39.0"
    val junit = "4.12"
    val koin = "2.2.3"
    val kotlin = "1.5.31"
    val lifecycle = "2.4.0"
    val markwon = "4.1.2"
    val material = "1.1.0-alpha08"
    val mockitoKotlin = "2.2.0"
    val recyclerView = "1.0.0"
    val retrofit = "2.9.0"
    val retrofitLogging = "4.9.1"
    val savedState = "1.0.0"
    val soLoader = "0.10.1"
    val splash = "1.0.0-alpha02"
    val sqlDelight = "1.5.2"
    val timber = "5.0.0-SNAPSHOT"
    val viewModel = "2.2.0"
}

object Plugins {
    val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
    val gradleAndroid = "com.android.tools.build:gradle:${Versions.gradleAndroid}"
    val gradleVersions = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersions}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
}

object Android {
    val accompanistAppCompat = "com.google.accompanist:accompanist-appcompat-theme:${Versions.accompanist}"
    val accompanistInsets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
    val accompanistInsetsUi = "com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}"
    val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    val browser = "androidx.browser:browser:${Versions.browser}"
    val compose = "androidx.compose.ui:ui:${Versions.compose}"
    val composeAnimation = "androidx.compose.animation:animation:${Versions.compose}"
    val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose}"
    val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    val composeMaterialIcons = "androidx.compose.material:material-icons-core:${Versions.compose}"
    val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val core = "androidx.core:core-ktx:${Versions.core}"
    val dataStorePrefs = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    val desugar = "com.android.tools:desugar_jdk_libs:${Versions.desugar}"
    val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    val material = "com.google.android.material:material:${Versions.material}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.savedState}"
    val splash = "androidx.core:core-splashscreen:${Versions.splash}"
    val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}"
}

object Kotlin {
    val coroutinesAndorid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
}

object Tools {
    val firebase = "com.google.firebase:firebase-core:${Versions.firebase}"
    val soLoader = "com.facebook.soloader:soloader:${Versions.soLoader}"
    val flipper = "com.facebook.flipper:flipper:${Versions.flipper}"
    val flipperNetwork = "com.facebook.flipper:flipper-network-plugin:${Versions.flipper}"
}

object Libraries {
    val adapterDepelegates = "com.hannesdorfmann:adapterdelegates4:${Versions.adapterDelegates}"
    val adapterDepelegatesDsl = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:${Versions.adapterDelegates}"
    val koin = "io.insert-koin:koin-android:${Versions.koin}"
    val markwon = "io.noties.markwon:core:${Versions.markwon}"
    val markwonLinkify = "io.noties.markwon:linkify:${Versions.markwon}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofitLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLogging}"
    val sqlDelightAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    val sqlDelightCoroutines = "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
    val timberAndroid = "com.jakewharton.timber:timber-android:${Versions.timber}"
    val timberJdk = "com.jakewharton.timber:timber-jdk:${Versions.timber}"
    val timber = arrayOf(timberAndroid, timberJdk)
}

object Tests {
    val assertk = "com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}"
    val junit = "junit:junit:${Versions.junit}"
    val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
}
