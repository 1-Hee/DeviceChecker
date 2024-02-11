import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.gms.oss-licenses-plugin")
    kotlin("kapt")// 사용하는 코틀린 버전에 맞게 해주어야함!
}

android {
    namespace = "kr.co.devicechecker"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.co.devicechecker"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "1.1.4"
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
            // signingConfig = signingConfigs
        }
        release {
            buildConfigField("boolean", "IS_DEBUG", "false")
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

    applicationVariants.all {
        val variant = this
        val currentDate = Date();
        val formattedDate = SimpleDateFormat("yyyy_MM_dd").format(currentDate)
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                if(output.outputFile != null){
                    if(output.outputFile.name.endsWith(".apk")){
                        val appPrefix = "test_it"
                        val versionName = variant.versionName
                        val buildType = variant.buildType.name
                        val outputName = "${appPrefix}_${buildType}_${formattedDate}_${versionName}.apk"
                        output.outputFileName = outputName
                    }
                }
            }
    }
    // https://developer88.tistory.com/338
    // https://support.bluestacks.com/hc/ko/articles/13353147110541-%EB%B8%94%EB%A3%A8%EC%8A%A4%ED%83%9D-5%EC%97%90%EC%84%9C-%EC%9D%B4-%EC%9E%A5%EC%B9%98%EB%8A%94-%ED%94%8C%EB%A0%88%EC%9D%B4-%ED%94%84%EB%A1%9C%ED%85%8D%ED%8A%B8-%EC%9D%B8%EC%A6%9D%EC%9D%84-%EB%B0%9B%EC%A7%80-%EB%AA%BB%ED%96%88%EC%8A%B5%EB%8B%88%EB%8B%A4-%EC%98%A4%EB%A5%98-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95
    kotlinOptions {
        jvmTarget = "11"
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
    val ted_version = "3.3.0"
    implementation("io.github.ParkSangGwon:tedpermission-normal:$ted_version")
    // gson
    val gson_version = "2.9.1"
    implementation("com.google.code.gson:gson:$gson_version")
    // https://developers.google.com/android/guides/opensource?hl=ko#kotlin-dsl
    val oss_version = "17.0.1"
    implementation("com.google.android.gms:play-services-oss-licenses:$oss_version")
}