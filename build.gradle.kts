// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("org.jetbrains.kotlinx.kover") version Versions.JetBrains.kover
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin(module = "gradle-plugin", version = Versions.JetBrains.kotlin))
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
