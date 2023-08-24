plugins {
    `kotlin-dsl`
}

group = "easydone.buildlogic"

dependencies {
    compileOnly("com.android.tools.build:gradle:7.3.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "easydone.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "easydone.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
    }
}
