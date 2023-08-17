object LibraryVersion {
    const val version = "0.3.0"
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover") version Versions.JetBrains.kover
    id("maven-publish")
}

base {
    archivesName.set("gson-kotlin-adapter")
    group = "com.livefront.gsonkotlinadapter"
    version = LibraryVersion.version
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        register<MavenPublication>("default") {
            groupId = "com.github.Livefront"
            artifactId = "gson-kotlin-adapter"
            version = LibraryVersion.version

            afterEvaluate {
                from(components["java"])
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
