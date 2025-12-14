import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") version "2.2.21"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ashvinprajapati.skillconnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ashvinprajapati.skillconnect"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.lombok)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    implementation (libs.gson)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.firebase.analytics)
    implementation (libs.lottie)
    implementation (libs.firebase.messaging)
    implementation(platform(libs.firebase.bom))
    implementation (libs.okhttp3.logging.interceptor)
    implementation(libs.circleimageview)
    implementation (libs.github.glide)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}