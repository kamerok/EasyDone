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
    implementation(Libraries.sqlDelightCoroutines)

    testImplementation(Tests.junit)
    testImplementation(Tests.assertk)
    testImplementation(Libraries.sqlDelightMemory)
}
