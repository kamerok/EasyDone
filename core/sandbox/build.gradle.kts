plugins {
    id("kotlin")
}

dependencies {
    implementation(projects.core.domain)

    implementation(Kotlin.coroutinesCore)
}
