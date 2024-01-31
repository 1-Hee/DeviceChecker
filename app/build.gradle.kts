plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")// 사용하는 코틀린 버전에 맞게 해주어야함!
}

android {
    namespace = "kr.co.devicechecker"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.co.devicechecker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugSignedKey") {
            storeFile = file("../app/keystore/deviceChecker.jks")
            storePassword = "device240131jwh@#!"
            keyAlias = "deviceKey"
            keyPassword = "keydevice240131jwh@#!"
        }

        create("releaseSignedKey") {
            storeFile = file("../app/keystore/deviceChecker.jks")
            storePassword = "device240131jwh@#!"
            keyAlias = "deviceKey"
            keyPassword = "keydevice240131jwh@#!"
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "IS_DEBUG", "false")

        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

//    // Specifies one flavor dimension.
//    flavorDimensions += "version"
//    productFlavors {
//        create("demo") {
//            // Assigns this product flavor to the "version" flavor dimension.
//            // If you are using only one dimension, this property is optional,
//            // and the plugin automatically assigns all the module's flavors to
//            // that dimension.
//            dimension = "version"
//            applicationIdSuffix = ".demo"
//            versionNameSuffix = "-demo"
//        }
//        create("full") {
//            dimension = "version"
//            applicationIdSuffix = ".full"
//            versionNameSuffix = "-full"
//            versionName = android.compileSdkVersion
//        }
//    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }



    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // multi dex
    implementation("androidx.multidex:multidex:2.0.1")

    // timber implements
    val tibmer_version = "4.7.1"
    implementation("com.jakewharton.timber:timber:$tibmer_version")

    // ted
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.3.0")

}