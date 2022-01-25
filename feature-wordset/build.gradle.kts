import com.myvocab.myvocab.Base
import com.myvocab.myvocab.dagger
import com.myvocab.myvocab.tests

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        minSdk = Base.minSDK
        targetSdk = Base.currentSDK
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":commonui"))
    implementation(project(":navigation"))
    dagger()
    tests()
}