buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(com.myvocab.myvocab.BuildPlugins.gradle)
        classpath(com.myvocab.myvocab.BuildPlugins.kotlin)
        classpath(com.myvocab.myvocab.BuildPlugins.gs)
        classpath(com.myvocab.myvocab.BuildPlugins.safeArgs)
        classpath(com.myvocab.myvocab.BuildPlugins.crashlytics)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

configurations.all {
    resolutionStrategy.force("org.antlr:antlr4-runtime:4.7.1")
    resolutionStrategy.force("org.antlr:antlr4-tool:4.7.1")
}
