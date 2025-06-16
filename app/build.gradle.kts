plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.product_detail"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.product_detail"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.lifecycle.viewmodel)
   // implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // Nếu muốn dùng StateFlow thay vì LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
}