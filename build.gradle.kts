plugins {
    id(Plugins.googleServices) version(Versions.googleServices) apply false
    id(Plugins.gradleAndroid) version(Versions.gradleAndroid) apply false
    id(Plugins.gradleVersions) version(Versions.gradleVersions)
    id(Plugins.kotlin) version(Versions.kotlin) apply false
    id(Plugins.kotlinxSerialization) version(Versions.kotlin) apply false
    id(Plugins.sqlDelight) version(Versions.sqlDelight) apply false
}

subprojects {
    afterEvaluate {
        (extensions.findByName("android") as? com.android.build.gradle.BaseExtension)?.apply {
            compileSdkVersion(Config.compileSdk)

            defaultConfig {
                minSdk = Config.minSdk
                targetSdk = Config.targetSdk
            }
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

apply {
    from(rootProject.file("gradle/dependency_graph.gradle"))
    from(rootProject.file("gradle/dependency_updates_settings.gradle"))
}