plugins {
    id("kotlin")
    id("kotlinx-serialization")
}


dependencies {
    api(projects.core.domain)
    implementation(projects.library.keyvalue)

    implementation(Kotlin.coroutinesCore)
    implementation(Kotlin.serialization)
    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitConverterKotlinx)
    implementation(Libraries.retrofitLogging)
}
