import com.myvocab.myvocab.Base
import com.myvocab.myvocab.dagger
import com.myvocab.myvocab.room
import com.myvocab.myvocab.tests

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        minSdk = Base.minSDK
        targetSdk = Base.currentSDK
    }

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":domain"))
    dagger()
    room()
    tests()
}