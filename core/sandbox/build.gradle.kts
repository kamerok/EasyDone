plugins {
    id("kotlin")
}

dependencies {
    implementation(project(":core:domain"))

    implementation(Kotlin.coroutinesCore)
}
