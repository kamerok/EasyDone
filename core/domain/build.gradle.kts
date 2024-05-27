plugins {
    id("kotlin")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(Libraries.timberJdk)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(Tests.assertk)
    testImplementation(Tests.junit)
}
