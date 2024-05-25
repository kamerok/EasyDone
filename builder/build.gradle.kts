plugins {
    alias(libs.plugins.easydone.android.library)
}

android {
    namespace = "com.kamer.builder"
}

dependencies {
    implementation(projects.widget)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.sandbox)
    implementation(projects.core.service.trello)
    implementation(projects.core.strings)
    implementation(projects.coreUi.design)

    implementation(projects.feature.edittask)
    implementation(projects.feature.home)
    implementation(projects.feature.inbox)
    implementation(projects.feature.quickcreatetask)
    implementation(projects.feature.settings)
    implementation(projects.feature.setupflow)
    implementation(projects.feature.taskdetails)
    implementation(projects.feature.waiting)

    implementation(projects.library.keyvalue)
    implementation(projects.library.keyvalue.impl)
    implementation(projects.library.navigation)

    implementation(Kotlin.coroutinesCore)
    implementation(Android.appCompat)
    implementation(Android.dataStorePrefs)
    implementation(Android.fragment)
    implementation(Android.splash)
    implementation(Libraries.koin)
    implementation(Libraries.retrofit)
    implementation(Libraries.sqlDelightAndroid)
}
