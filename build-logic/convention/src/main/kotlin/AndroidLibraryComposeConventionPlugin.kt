
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("easydone.android.library")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.14"
                }
            }
        }
    }

}
