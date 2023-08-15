plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.0")
    implementation(kotlin(module = "gradle-plugin", version = "1.8.21"))
    implementation("com.squareup:javapoet:1.13.0")
}
