import com.myvocab.myvocab.Base
import com.myvocab.myvocab.dagger
import com.myvocab.myvocab.glide
import com.myvocab.myvocab.tests

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        applicationId = "com.myvocab.myvocab"
        minSdk = Base.minSDK
        targetSdk = Base.currentSDK
        versionCode = Base.versionCode
        versionName = Base.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        testOptions.unitTests.isIncludeAndroidResources = true
    }

    buildTypes {
        val USE_LEAK_CANARY = "USE_LEAK_CANARY"
        val EXACT_REMINDING = "EXACT_REMINDING"

        getByName("debug") {
            (this as ExtensionAware).extra["alwaysUpdateBuildId"] = false
            manifestPlaceholders["enableCrashReporting"] = false
            addBooleanConfigField(USE_LEAK_CANARY, false)
            addBooleanConfigField(EXACT_REMINDING, true)
        }

        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["enableCrashReporting"] = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            addBooleanConfigField(USE_LEAK_CANARY, false)
            addBooleanConfigField(EXACT_REMINDING, false)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

fun com.android.build.api.dsl.BuildType.addBooleanConfigField(name: String, value: Boolean) {
    buildConfigField("Boolean", name, value.toString())
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":navigation"))
    implementation(project(":feature-fast-translation"))
    implementation(project(":feature-learning"))
    implementation(project(":feature-wordset"))
    implementation(project(":feature-settings"))
    implementation(project(":commonui"))

    dagger()
    glide()
    tests()
}
repositories {
    mavenCentral()
}
configurations {
    all {
        exclude(module = "httpclient")
        exclude(module = "commons-logging")
    }
}
apply(plugin = "com.google.gms.google-services")
