// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("org.jetbrains.kotlinx.kover") version Versions.JetBrains.kover
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath(kotlin(module = "gradle-plugin", version = Versions.JetBrains.kotlin))
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.Google.hilt}")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
