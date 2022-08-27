plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kamer.easydone"

    defaultConfig {
        applicationId = "com.kamer.easydone"
        versionCode = 1
        versionName = "1.0"

        //intended to be publicly accessible
        buildConfigField("String", "TRELLO_API_KEY", "\"98c9ac26156a960889eb42586aa1bcd7\"")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystore/debug.keystore")
            storePassword = "123456"
            keyAlias = "key"
            keyPassword = "123456"
        }
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        val debug by getting {
            (this as ExtensionAware).extra["alwaysUpdateBuildId"] = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    coreLibraryDesugaring(Android.desugar)

    implementation(project(":builder"))
    implementation(project(":core-ui:design"))

    implementation(Android.appCompat)
    implementation(Android.splash)
    implementation(Libraries.retrofit)
    implementation(Libraries.timberAndroid)
    implementation(Tools.firebase)
    debugImplementation(Tools.flipper)
    debugImplementation(Tools.flipperNetwork)
    debugImplementation(Tools.soLoader)
}
