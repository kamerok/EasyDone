plugins {
    id("kotlin")
    id("kotlinx-serialization")
}


dependencies {
    api(project(":core:domain"))
    implementation(project(":library:keyvalue"))

    implementation(Kotlin.coroutinesCore)
    implementation(Kotlin.serialization)
    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitConverterKotlinx)
    implementation(Libraries.retrofitLogging)
}
