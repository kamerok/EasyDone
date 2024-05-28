plugins {
    id("kotlin")
    id("com.squareup.sqldelight")
}

sqldelight {
    database("Database") {
        packageName = "easydone.core.database"
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.sqldelight.coroutines)

    testImplementation(libs.test.assertk)
    testImplementation(libs.test.junit)
    testImplementation(libs.sqldelight.memory)
}
