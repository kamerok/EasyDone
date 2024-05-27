plugins {
    id("kotlin")
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.kotlinx.coroutines)
}
