plugins {
    id("kotlin")
    id("kotlinx-serialization")
}


dependencies {
    api(projects.core.domain)
    implementation(projects.library.keyvalue)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitConverterKotlinx)
    implementation(Libraries.retrofitLogging)
}
