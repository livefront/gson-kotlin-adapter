plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin(module = "gradle-plugin", version = "1.8.21"))
}
