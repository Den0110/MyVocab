package com.myvocab.myvocab

import org.gradle.api.artifacts.dsl.DependencyHandler

object Deps {

    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogging}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxRetrofitAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.rxRetrofitAdapter}"
    const val reactiveStreams = "android.arch.lifecycle:reactivestreams:${Versions.reactiveStreams}"
    const val openCsv = "com.opencsv:opencsv:${Versions.openCsv}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val targetPrompt = "uk.co.samuelwall:material-tap-target-prompt:${Versions.targetPrompt}"
    const val appIntro = "com.github.AppIntro:AppIntro:${Versions.appIntro}"
    const val multiDex = "com.android.support:multidex:${Versions.multiDex}"
    const val leakCanary = "com.squareup.leakcanary:plumber-android:${Versions.leakCanary}"

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"
        const val playServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}"
    }

    object AndroidX {
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}"
        const val legacySupport = "androidx.legacy:legacy-support-v4:${Versions.AndroidX.legacySupport}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
        const val cardView = "androidx.cardview:cardview:${Versions.AndroidX.cardView}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
        const val room = "androidx.room:room-rxjava2:${Versions.room}"
        const val roomCoroutines = "androidx.room:room-ktx:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
        const val lifecycleExt = "androidx.lifecycle:lifecycle-extensions:${Versions.AndroidX.lifecycleExt}"
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.lifecycle}"
        const val lifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:${Versions.AndroidX.lifecycle}"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:${Versions.Firebase.bom}"
        const val firestore = "com.google.firebase:firebase-firestore-ktx"
        const val analytics = "com.google.firebase:firebase-analytics"
        const val crashlytics = "com.google.firebase:firebase-crashlytics"
        const val config = "com.google.firebase:firebase-config-ktx"
        const val playServicesAds = "com.google.android.gms:play-services-ads:${Versions.Firebase.playServicesAds}"
        const val rx = "com.github.FrangSierra:RxFirebase:${Versions.Firebase.rx}"
    }
    
    object Navigation {
        const val fragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val ui = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"
    }
    
    object Dagger {
        const val main = "com.google.dagger:dagger:${Versions.dagger}"
        const val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        const val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val androidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object Glide {
        const val main = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    }

    object Retrofit {
        const val main = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val converter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
        const val rx2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    }

    object Tests {
        const val junit = "junit:junit:${Versions.Tests.junit}"
        const val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
        const val androidCore = "androidx.test:core:${Versions.Tests.androidCore}"
        const val androidExt = "androidx.test.ext:junit:${Versions.Tests.androidExt}"
        const val androidTestRunner = "androidx.test:runner:${Versions.Tests.androidTestRunner}"
        const val robolectric = "org.robolectric:robolectric:${Versions.Tests.robolectric}"
        const val mockito = "org.mockito:mockito-core:${Versions.Tests.mockito}"
    }
}

fun DependencyHandler.room() {
    implementation(Deps.AndroidX.room)
    implementation(Deps.AndroidX.roomCoroutines)
    kapt(Deps.AndroidX.roomCompiler)
}

fun DependencyHandler.dagger() {
    implementation(Deps.Dagger.main)
    implementation(Deps.Dagger.android)
    implementation(Deps.Dagger.androidSupport)
    kapt(Deps.Dagger.compiler)
    kapt(Deps.Dagger.androidProcessor)
}

fun DependencyHandler.glide() {
    implementation(Deps.Glide.main)
    kapt(Deps.Glide.compiler)
}

fun DependencyHandler.tests() {
    testImplementation(Deps.Tests.junit)
    testImplementation(Deps.Tests.kotlinJunit)
    testImplementation(Deps.Tests.androidCore)
    testImplementation(Deps.Tests.robolectric)
    testImplementation(Deps.Tests.mockito)
    androidTestImplementation(Deps.Tests.androidExt)
    androidTestImplementation(Deps.Tests.androidTestRunner)
}

private fun DependencyHandler.implementation(depName: String) {
    add("implementation", depName)
}
private fun DependencyHandler.testImplementation(depName: String) {
    add("testImplementation", depName)
}
private fun DependencyHandler.androidTestImplementation(depName: String) {
    add("androidTestImplementation", depName)
}
private fun DependencyHandler.kapt(depName: String) {
    add("kapt", depName)
}
private fun DependencyHandler.compileOnly(depName: String) {
    add("compileOnly", depName)
}
private fun DependencyHandler.api(depName: String) {
    add("api", depName)
}