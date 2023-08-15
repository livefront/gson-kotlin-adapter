plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.livefront.sample"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.livefront.gsonkotlinadapter.sample"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":library"))
    implementation(kotlin(module = "stdlib", version = Versions.JetBrains.kotlin))
    implementation(kotlin(module = "reflect", version = Versions.JetBrains.kotlin))
    implementation("com.google.code.gson:gson:${Versions.Google.gson}")
    implementation("androidx.activity:activity-ktx:${Versions.AndroidX.activity}")
    implementation("androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}")
    implementation("androidx.core:core-ktx:${Versions.AndroidX.core}")
    implementation("com.squareup.okhttp3:okhttp:${Versions.Square.okHttp}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.Square.okHttp}")
    implementation("com.squareup.okio:okio:${Versions.Square.okio}")
    implementation("com.squareup.retrofit2:retrofit:${Versions.Square.retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${Versions.Square.retrofit}")

    implementation("com.google.dagger:hilt-android:${Versions.Google.hilt}")
    kapt("com.google.dagger:hilt-compiler:${Versions.Google.hilt}")
}
