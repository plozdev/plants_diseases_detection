import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.plantdiseasedetection"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.plantdiseasedetection"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        buildConfigField(
            "String",
            "GOOGLE_API_KEY",
            "\"${properties.getProperty("GOOGLE_API_KEY")}\""
        )
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${properties.getProperty("GEMINI_API_KEY")}\""
        )
        buildConfigField(
            "String",
            "WEATHER_API_KEY",
            "\"${properties.getProperty("WEATHER_API_KEY")}\""
        )


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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
    }


    implementation(libs.viewpager2)
    implementation(libs.material)
    // --- Core AndroidX & UI ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)

    // --- MVVM architecture ---
//    val lifecycleVersion = "2.8.7"
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.room.compiler)
    // Annotation processor
    annotationProcessor(libs.androidx.lifecycle.compiler)


    // --- Camera X ---
//    val camerax_version = "1.3.1"
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.camera.view)
    implementation(libs.guava)

    // To use CallbackToFutureAdapter
    implementation(libs.concurrent.futures)

    // --- Image loading ---
    implementation(libs.glide)


    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.google.googleid)
    implementation(libs.play.services.auth)
    //Retrofit
    implementation(libs.retrofit)
    implementation (libs.com.squareup.retrofit2.converter.gson)
    implementation (libs.gson)
    implementation (libs.play.services.location)
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp)
    implementation (libs.dexter)



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}