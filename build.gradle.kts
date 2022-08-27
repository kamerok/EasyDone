buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.gradleAndroid)
        classpath(Plugins.gradleVersions)
        classpath(Plugins.kotlin)
        classpath(Plugins.kotlinxSerialization)
        classpath(Plugins.sqlDelight)
        classpath(Plugins.googleServices)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
    }
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
    from(rootProject.file("gradle/dependency_updates.gradle"))
}
