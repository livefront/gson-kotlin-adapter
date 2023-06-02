buildscript {
    // Just need to define this
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover") version Versions.JetBrains.kover
}

base {
    archivesName.set("gson-kotlin-adapter")
    group = "com.livefront.gsonkotlinadapter"
    version = "0.2.0"
}

dependencies {
    implementation(kotlin(module = "stdlib", version = Versions.JetBrains.kotlin))
    implementation(kotlin(module = "reflect", version = Versions.JetBrains.kotlin))
    implementation("com.google.code.gson:gson:${Versions.Google.gson}")

    testImplementation("com.google.code.gson:gson:${Versions.Google.gson}")
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.Junit.jupiter}")
    testImplementation("io.mockk:mockk:${Versions.Mockk.mockk}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
