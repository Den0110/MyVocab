import com.android.build.api.dsl.BuildType
import com.myvocab.myvocab.*

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

    buildTypes.forEach {
        it.addStringConfigField("GOOGLE_API_BASE_URL", "https://translation.googleapis.com")
        it.addStringConfigFieldFromProps("GOOGLE_API_KEY", "GoogleAPIKey")

        it.addStringConfigField("YANDEX_TRANSLATE_API_BASE_URL", "https://translate.yandex.net")
        it.addStringConfigFieldFromProps("YANDEX_TRANSLATE_API_KEY", "YandexTranslateAPIKey")

        it.addStringConfigField("YANDEX_DICTIONARY_API_BASE_URL", "https://dictionary.yandex.net")
        it.addStringConfigFieldFromProps("YANDEX_DICTIONARY_API_KEY", "YandexDictionaryAPIKey")
    }

    buildTypes {

        val USE_LEAK_CANARY = "USE_LEAK_CANARY"
        val EXACT_REMINDING = "EXACT_REMINDING"

        getByName("debug") {
            addBooleanConfigField(USE_LEAK_CANARY, false)
            addBooleanConfigField(EXACT_REMINDING, true)
        }

        getByName("release") {
            addBooleanConfigField(USE_LEAK_CANARY, false)
            addBooleanConfigField(EXACT_REMINDING, false)
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
    dagger()
    glide()

    api(Deps.reflect)
    api(Deps.rxBinding)
    api(Deps.material)
    api(Deps.moshi)
    api(Deps.okHttpLogging)
    api(Deps.rxJava)
    api(Deps.rxAndroid)
    api(Deps.rxRetrofitAdapter)
    api(Deps.reactiveStreams)
    api(Deps.openCsv)
    api(Deps.timber)
    api(Deps.targetPrompt)
    api(Deps.appIntro)
    api(Deps.multiDex)
    api(Deps.leakCanary)

    api(Deps.Coroutines.core)
    api(Deps.Coroutines.android)
    api(Deps.Coroutines.rx2)
    api(Deps.Coroutines.playServices)

    api(Deps.AndroidX.core)
    api(Deps.AndroidX.appCompat)
    api(Deps.AndroidX.legacySupport)
    api(Deps.AndroidX.constraintLayout)
    api(Deps.AndroidX.cardView)
    api(Deps.AndroidX.recyclerView)
    api(Deps.AndroidX.fragment)
    api(Deps.AndroidX.lifecycleExt)
    api(Deps.AndroidX.lifecycleViewModel)
    api(Deps.AndroidX.lifecycleRuntime)
    api(Deps.AndroidX.lifecycleCommon)

    api(platform(Deps.Firebase.BOM))
    api(Deps.Firebase.firestore)
    api(Deps.Firebase.analytics)
    api(Deps.Firebase.crashlytics)
    api(Deps.Firebase.config)
    api(Deps.Firebase.playServicesAds)
    api(Deps.Firebase.rx)

    api(Deps.Navigation.fragment)
    api(Deps.Navigation.ui)

    api(Deps.Retrofit.main)
    api(Deps.Retrofit.converter)
    api(Deps.Retrofit.rx2)

    tests()
}