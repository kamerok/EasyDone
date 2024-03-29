plugins {
    id("easydone.android.library")
}

android {
    namespace = "com.kamer.builder"
}

dependencies {
    implementation(project(":widget"))

    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:sandbox"))
    implementation(project(":core:service:trello"))
    implementation(project(":core:strings"))
    implementation(project(":core-ui:design"))

    implementation(project(":feature:edittask"))
    implementation(project(":feature:home"))
    implementation(project(":feature:inbox"))
    implementation(project(":feature:quickcreatetask"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:setupflow"))
    implementation(project(":feature:taskdetails"))
    implementation(project(":feature:waiting"))

    implementation(project(":library:keyvalue"))
    implementation(project(":library:keyvalue:impl"))
    implementation(project(":library:navigation"))

    implementation(Kotlin.coroutinesCore)
    implementation(Android.appCompat)
    implementation(Android.dataStorePrefs)
    implementation(Android.fragment)
    implementation(Android.savedState)
    implementation(Android.splash)
    implementation(Libraries.koin)
    implementation(Libraries.retrofit)
    implementation(Libraries.sqlDelightAndroid)
}
