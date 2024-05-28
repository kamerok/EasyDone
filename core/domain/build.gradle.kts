plugins {
    id("kotlin")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(libs.timber.jdk)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.test.assertk)
    testImplementation(libs.test.junit)
}
