plugins {
    id("kotlin")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("easydone.core.database")
        }
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
