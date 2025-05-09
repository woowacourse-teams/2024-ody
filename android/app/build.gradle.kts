import java.util.Properties

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.android.junit.jupiter)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.mulberry.ody"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mulberry.ody"
        minSdk = 26
        targetSdk = 34
        versionCode = 14
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_DEV_URL", properties["BASE_DEV_URL"].toString())
        buildConfigField("String", "BASE_PROD_URL", properties["BASE_PROD_URL"].toString())
        buildConfigField("String", "KAKAO_API_KEY", properties["KAKAO_API_KEY"].toString())
        buildConfigField("String", "KAKAO_NATIVE_KEY", properties["KAKAO_NATIVE_KEY"].toString())
        buildConfigField(
            "String",
            "PRIVACY_POLICY_URI",
            properties["PRIVACY_POLICY_URI"].toString(),
        )
        buildConfigField("String", "TERM_URI", properties["TERM_URI"].toString())
        manifestPlaceholders["KAKAO_NATIVE_KEY"] =
            properties["KAKAO_NATIVE_KEY"].toString().trim('"')
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.view.pager)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.fragment.testing)
    kapt(libs.androidx.hilt.compiler)

    // firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging.ktx)
    implementation(platform(libs.firebase.bom))

    // junit
    testImplementation(libs.junit.jupiter)

    // assertj
    testImplementation(libs.assertj)

    // material
    implementation(libs.material)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // timber
    implementation(libs.timber)

    // dotsindicator
    implementation(libs.dotsindicator)

    // play service
    implementation(libs.play.services.location)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // glide
    implementation(libs.glide)

    // kakao share
    implementation(libs.kakao.share)

    // kakao sdk
    implementation(libs.kakao.sdk.v2.user)

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    // paging
    implementation(libs.androidx.paging)
    implementation(libs.androidx.compose.paging)
}
