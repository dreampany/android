package com.dreampany.hi.inject

import android.content.Context
import com.dreampany.common.inject.qualifier.Nearby
import com.dreampany.common.inject.qualifier.Remote
import com.dreampany.hi.data.source.api.UserDataSource
import com.dreampany.hi.data.source.mapper.UserMapper
import com.dreampany.hi.data.source.nearby.UserNearbyDataSource
import com.dreampany.hi.data.source.remote.UserRemoteDataSource
import com.dreampany.network.nearby.NearbyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


/**
 * Created by roman on 7/10/21
 * Copyright (c) 2021 butler. All rights reserved.
 * ifte.net@gmail.com
 * Last modified $file.lastModified
 */
@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Nearby
    @Provides
    fun nearby(
        @ApplicationContext context : Context,
        mapper : UserMapper,
        nearby: NearbyManager
    ): UserDataSource = UserNearbyDataSource(context, mapper, nearby)

    @Remote
    @Provides
    fun remote(
    ): UserDataSource = UserRemoteDataSource()
}