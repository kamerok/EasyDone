plugins {
    id("kotlin")
    id("kotlinx-serialization")
}


dependencies {
    api(projects.core.domain)
    implementation(projects.library.keyvalue)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx)
    implementation(libs.retrofit.logging)
}
