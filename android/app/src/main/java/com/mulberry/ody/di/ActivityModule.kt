package com.mulberry.ody.di

import android.content.Context
import com.mulberry.ody.presentation.common.image.ImageShareHelper
import com.mulberry.ody.presentation.common.image.KakaoImageShareHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    fun provideImageHelper(
        @ActivityContext context: Context,
    ): ImageShareHelper {
        return KakaoImageShareHelper(context)
    }
}
