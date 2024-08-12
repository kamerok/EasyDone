plugins {
    alias(libs.plugins.gradle.doctor)
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.sql.delight) apply false
}

task<Delete>("clean") {
    delete(layout.buildDirectory)
}

//apply {
//    from(rootProject.file("gradle/dependency_graph.gradle"))
//    from(rootProject.file("gradle/dependency_updates_settings.gradle"))
//}
