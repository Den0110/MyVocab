import com.android.build.api.dsl.BuildType
import com.myvocab.myvocab.Base
import com.myvocab.myvocab.Deps

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

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

    }
    buildTypes {

        val USE_LEAK_CANARY = "USE_LEAK_CANARY"
        val EXACT_REMINDING = "EXACT_REMINDING"

        buildTypes.forEach {
            it.addStringConfigField("GOOGLE_API_BASE_URL", "https://translation.googleapis.com")
            it.addStringConfigFieldFromProps("GOOGLE_API_KEY", "GoogleAPIKey")

            it.addStringConfigField("YANDEX_TRANSLATE_API_BASE_URL", "https://translate.yandex.net")
            it.addStringConfigFieldFromProps("YANDEX_TRANSLATE_API_KEY", "YandexTranslateAPIKey")

            it.addStringConfigField("YANDEX_DICTIONARY_API_BASE_URL", "https://dictionary.yandex.net")
            it.addStringConfigFieldFromProps("YANDEX_DICTIONARY_API_KEY", "YandexDictionaryAPIKey")
        }

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

fun BuildType.addStringConfigFieldFromProps(fieldName: String, propName: String) {
    addStringConfigField(fieldName, properties[propName].toString())
}

fun BuildType.addStringConfigField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}

fun BuildType.addBooleanConfigField(name: String, value: Boolean) {
    buildConfigField("Boolean", name, value.toString())
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Deps.reflect)
    implementation(Deps.rxBinding)
    implementation(Deps.material)
    implementation(Deps.moshi)
    implementation(Deps.okHttpLogging)
    implementation(Deps.rxJava)
    implementation(Deps.rxAndroid)
    implementation(Deps.rxRetrofitAdapter)
    implementation(Deps.reactiveStreams)
    implementation(Deps.openCsv)
    implementation(Deps.timber)
    implementation(Deps.targetPrompt)
    implementation(Deps.appIntro)
    implementation(Deps.multiDex)
    implementation(Deps.leakCanary)

    implementation(Deps.Coroutines.android)
    implementation(Deps.Coroutines.rx2)
    implementation(Deps.Coroutines.playServices)

    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.AndroidX.legacySupport)
    implementation(Deps.AndroidX.constraintLayout)
    implementation(Deps.AndroidX.cardView)
    implementation(Deps.AndroidX.recyclerView)
    implementation(Deps.AndroidX.room)
    kapt(Deps.AndroidX.roomCompiler)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.lifecycleExt)
    implementation(Deps.AndroidX.lifecycleViewModel)
    implementation(Deps.AndroidX.lifecycleRuntime)
    implementation(Deps.AndroidX.lifecycleCommon)

    implementation(platform(Deps.Firebase.BOM))
    implementation(Deps.Firebase.firestore)
    implementation(Deps.Firebase.analytics)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.Firebase.config)
    implementation(Deps.Firebase.playServicesAds)
    implementation(Deps.Firebase.rx)

    implementation(Deps.Navigation.fragment)
    implementation(Deps.Navigation.ui)

    implementation(Deps.Dagger.main)
    implementation(Deps.Dagger.android)
    implementation(Deps.Dagger.androidSupport)
    kapt(Deps.Dagger.compiler)
    kapt(Deps.Dagger.androidProcessor)

    implementation(Deps.Glide.main)
    kapt(Deps.Glide.compiler)

    implementation(Deps.Retrofit.main)
    implementation(Deps.Retrofit.converter)
    implementation(Deps.Retrofit.rx2)

    testImplementation(Deps.Tests.junit)
    testImplementation(Deps.Tests.kotlinJunit)
    testImplementation(Deps.Tests.androidCore)
    androidTestImplementation(Deps.Tests.androidExt)
    androidTestImplementation(Deps.Tests.androidTestRunner)
    testImplementation(Deps.Tests.robolectric)
    testImplementation(Deps.Tests.mockito)
}
repositories {
    mavenCentral()
}
configurations {
    all {
        exclude(module = "httpclient")
        exclude(module =  "commons-logging")
    }
}
apply(plugin = "com.google.gms.google-services")
