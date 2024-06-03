plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.iot_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iot_project"
        minSdk = 21
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation ("com.github.angads25:toggle:1.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0")
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
}