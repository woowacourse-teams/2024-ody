# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# kakao share
-keep class com.kakao.sdk.**.model.* { *; }
-keep class * extends com.google.gson.TypeAdapter

# https://github.com/square/okhttp/pull/6792
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# retrofit
-keep class com.mulberry.ody.data.retrofit.* { *; }
-keep class com.mulberry.ody.domain.apiresult.ApiResult
-keep class com.mulberry.ody.data.remote.core.service.* { *; }

# moshi
-keep class com.squareup.moshi.** { *; }
-keep class com.mulberry.ody.data.remote.thirdparty.address.response.* { *; }
-keep class com.mulberry.ody.data.remote.thirdparty.address.response.coord.* { *; }