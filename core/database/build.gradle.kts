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
    implementation(project(":core:domain"))

    implementation(Kotlin.coroutinesCore)
    implementation(Libraries.sqlDelightCoroutines)

    testImplementation(Tests.junit)
    testImplementation(Tests.assertk)
}
