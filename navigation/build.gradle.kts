
import com.myvocab.myvocab.Base
import com.myvocab.myvocab.Deps
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
}

dependencies {
    implementation(project(":core"))
    implementation(Deps.Navigation.fragment)
    implementation(Deps.Navigation.ui)
    dagger()
    tests()
}