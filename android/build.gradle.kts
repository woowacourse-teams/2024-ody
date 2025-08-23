plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
