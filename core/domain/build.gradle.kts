plugins {
    id("kotlin")
}

dependencies {
    implementation(Kotlin.coroutinesCore)
    implementation(Libraries.timberJdk)

    testImplementation(Kotlin.coroutinesTest)
    testImplementation(Tests.assertk)
    testImplementation(Tests.junit)
}
