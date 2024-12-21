package com.mulberry.ody.di

import com.mulberry.ody.data.auth.source.local.DefaultLocalAuthDataSource
import com.mulberry.ody.data.auth.source.local.LocalAuthDataSource
import com.mulberry.ody.data.auth.source.remote.DefaultRemoteAuthDataSource
import com.mulberry.ody.data.auth.source.remote.RemoteAuthDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Binds
    @Singleton
    fun bindRemoteAuthDataSource(defaultRemoteAuthDataSource: DefaultRemoteAuthDataSource): RemoteAuthDataSource

    @Binds
    @Singleton
    fun bindLocalAuthDataSource(defaultLocalAuthDataSource: DefaultLocalAuthDataSource): LocalAuthDataSource
}
