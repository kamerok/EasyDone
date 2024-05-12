pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "EasyDone"

include(":app")
include(":builder")
include(":widget")

include(":core-ui:design")
include(":core:database")
include(":core:domain")
include(":core:sandbox")
include(":core:service:trello")
include(":core:strings")
include(":core:utils")

include(":feature:edittask")
include(":feature:home")
include(":feature:inbox")
include(":feature:login")
include(":feature:quickcreatetask")
include(":feature:selectboard")
include(":feature:selecttype")
include(":feature:settings")
include(":feature:setupflow")
include(":feature:taskdetails")
include(":feature:waiting")

include(":library:keyvalue")
include(":library:keyvalue:impl")
include(":library:navigation")
