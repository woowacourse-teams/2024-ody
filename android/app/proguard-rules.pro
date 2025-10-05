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
-dontwarn com.google.gson.Gson
-dontwarn com.google.gson.JsonElement
-dontwarn com.google.gson.JsonObject
-dontwarn com.google.gson.annotations.JsonAdapter
-dontwarn com.google.gson.annotations.SerializedName
-keep class com.kakao.sdk.**.model.* { *; }
-dontwarn com.kakao.sdk.common.json.MapToQueryAdapter

# https://github.com/square/okhttp/pull/6792
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# retrofit
-keep class com.mulberry.ody.data.retrofit.* { *; }
-keep class com.mulberry.ody.domain.apiresult.ApiResult
-keep class com.mulberry.ody.data.remote.core.service.* { *; }
-keep class com.mulberry.ody.data.remote.core.entity.* { *; }
-keep class com.mulberry.ody.data.local.entity.eta.* { *; }
-keep class com.mulberry.ody.domain.model.* { *; }

# serialization
-keep class com.mulberry.ody.data.remote.thirdparty.address.response.* { *; }
-keep class com.mulberry.ody.data.remote.thirdparty.address.response.coord.* { *; }
