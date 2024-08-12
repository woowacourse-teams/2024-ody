plugins {
    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.plugin.parcelize) apply false
}
