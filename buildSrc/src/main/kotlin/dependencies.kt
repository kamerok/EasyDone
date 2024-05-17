@file:Suppress("MayBeConstant")

object Config {
    val compileSdk = 34
    val minSdk = 28
    val targetSdk = 34
}

object Versions {
    val accompanist = "0.25.1"
    val activityCompose = "1.9.0"
    val appCompat = "1.6.1"
    val assertk = "0.28.1"
    val browser = "1.6.0"
    val compose = "1.5.0"
    val composeCompiler = "1.5.14"
    val constraintLayout = "2.1.4"
    val core = "1.13.1"
    val coroutines = "1.8.1"
    val crashlytics = "19.0.0"
    val crashlyticsPlugin = "3.0.1"
    val dataStore = "1.0.0"
    val desugar = "2.0.4"
    val firebase = "21.1.1"
    val flipper = "0.250.0"
    val fragment = "1.7.0"
    val glance = "1.0.0-alpha01"
    val googleServices = "4.4.1"
    val gradleAndroid = "8.4.0"
    val gradleVersions = "0.51.0"
    val junit = "4.13.2"
    val koin = "2.2.3"
    val kotlin = "1.9.24"
    val kotlinxSerialization = "1.6.3"
    val lifecycle = "2.6.1"
    val markdown = "0.5.0"
    val mockitoKotlin = "2.2.0"
    val retrofit = "2.11.0"
    val retrofitKotlinxSerialization = "1.0.0"
    val retrofitLogging = "4.12.0"
    val savedState = "1.0.0"
    val soLoader = "0.11.0"
    val splash = "1.0.1"
    val sqlDelight = "1.5.5"
    val timber = "5.0.0-SNAPSHOT"
    val viewModel = "2.2.0"
}

object Plugins {
    val crashlytics = "com.google.firebase.crashlytics"
    val googleServices = "com.google.gms.google-services"
    val gradleAndroid = "com.android.application"
    val gradleVersions = "com.github.ben-manes.versions"
    val kotlin = "org.jetbrains.kotlin.android"
    val kotlinxSerialization = "org.jetbrains.kotlin.plugin.serialization"
    val sqlDelight = "com.squareup.sqldelight"
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
    val composeIcons = "androidx.compose.material:material-icons-extended:${Versions.compose}"
    val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    val composeMaterialIcons = "androidx.compose.material:material-icons-core:${Versions.compose}"
    val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val core = "androidx.core:core-ktx:${Versions.core}"
    val dataStorePrefs = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    val desugar = "com.android.tools:desugar_jdk_libs:${Versions.desugar}"
    val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    val glance = "androidx.glance:glance-appwidget:${Versions.glance}"
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.savedState}"
    val splash = "androidx.core:core-splashscreen:${Versions.splash}"
    val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}"
}

object Kotlin {
    val coroutinesAndorid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
}

object Tools {
    val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:${Versions.crashlytics}"
    val firebase = "com.google.firebase:firebase-core:${Versions.firebase}"
    val soLoader = "com.facebook.soloader:soloader:${Versions.soLoader}"
    val flipper = "com.facebook.flipper:flipper:${Versions.flipper}"
    val flipperNetwork = "com.facebook.flipper:flipper-network-plugin:${Versions.flipper}"
}

object Libraries {
    val koin = "io.insert-koin:koin-android:${Versions.koin}"
    val markdown = "com.github.jeziellago:compose-markdown:${Versions.markdown}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitConverterKotlinx = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofitKotlinxSerialization}"
    val retrofitLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLogging}"
    val sqlDelightAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    val sqlDelightCoroutines = "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
    val sqlDelightMemory = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    val timberAndroid = "com.jakewharton.timber:timber-android:${Versions.timber}"
    val timberJdk = "com.jakewharton.timber:timber-jdk:${Versions.timber}"
}

object Tests {
    val assertk = "com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}"
    val junit = "junit:junit:${Versions.junit}"
    val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
}
