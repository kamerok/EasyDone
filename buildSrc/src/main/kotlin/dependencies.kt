@file:Suppress("MayBeConstant")

object Config {
    val compileSdk = 34
    val minSdk = 28
    val targetSdk = 34
}

object Versions {
    val assertk = "0.28.1"
    val junit = "4.13.2"
    val koin = "2.2.3"
    val kotlin = "2.0.0"
    val markdown = "0.5.0"
    val retrofit = "2.11.0"
    val retrofitKotlinxSerialization = "1.0.0"
    val retrofitLogging = "4.12.0"
    val sqlDelight = "1.5.5"
    val timber = "5.0.0-SNAPSHOT"
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
}
