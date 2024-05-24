plugins {
    id(Plugins.googleServices) version(Versions.googleServices) apply false
    id(Plugins.composeCompiler) version(Versions.kotlin) apply false
    id(Plugins.crashlytics) version(Versions.crashlyticsPlugin) apply false
    id(Plugins.gradleAndroid) version(Versions.gradleAndroid) apply false
    id(Plugins.gradleVersions) version(Versions.gradleVersions)
    id(Plugins.kotlin) version(Versions.kotlin) apply false
    id(Plugins.kotlinxSerialization) version(Versions.kotlin) apply false
    id(Plugins.sqlDelight) version(Versions.sqlDelight) apply false
    id(Plugins.gradleDoctor) version (Versions.gradleDoctor)
}

task<Delete>("clean") {
    delete(layout.buildDirectory)
}

apply {
    from(rootProject.file("gradle/dependency_graph.gradle"))
    from(rootProject.file("gradle/dependency_updates_settings.gradle"))
}
