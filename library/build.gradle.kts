buildscript {
    // Just need to define this
}

plugins {
    kotlin("jvm")
}

apply(from = rootProject.file("gradle/jacoco.gradle"))

base {
    archivesName.set("gson-kotlin-adapter")
    group = "com.livefront.gsonkotlinadapter"
    version = "0.2.0"
}

dependencies {
    compileOnly(kotlin(module = "stdlib", version = Versions.JetBrains.kotlin))
    compileOnly(kotlin(module = "reflect", version = Versions.JetBrains.kotlin))
    compileOnly("com.google.code.gson:gson:${Versions.Google.gson}")

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
