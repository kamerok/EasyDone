plugins {
    alias(libs.plugins.easydone.android.application)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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

    buildFeatures {
        buildConfig = true
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
    coreLibraryDesugaring(libs.android.desugar)

    implementation(projects.builder)
    implementation(projects.coreUi.design)

    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.splash)
    implementation(libs.retrofit)
    implementation(libs.timber.android)
    implementation(libs.crashlytics)
    implementation(libs.firebase)
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipper.network)
    debugImplementation(libs.soLoader)
}
