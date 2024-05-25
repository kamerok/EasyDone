plugins {
    alias(libs.plugins.android.application)
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.kamer.easydone"

    compileSdk = Config.compileSdk
    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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

    implementation(projects.builder)
    implementation(projects.coreUi.design)

    implementation(Android.appCompat)
    implementation(Android.splash)
    implementation(Libraries.retrofit)
    implementation(Libraries.timberAndroid)
    implementation(libs.crashlytics)
    implementation(Tools.firebase)
    debugImplementation(Tools.flipper)
    debugImplementation(Tools.flipperNetwork)
    debugImplementation(Tools.soLoader)
}
