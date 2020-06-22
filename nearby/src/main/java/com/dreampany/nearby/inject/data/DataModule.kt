package com.dreampany.nearby.inject.data

import android.content.Context
import android.os.UserManager
import com.dreampany.framework.inject.annote.Nearby
import com.dreampany.framework.inject.data.StoreModule
import com.dreampany.nearby.data.source.api.UserDataSource
import com.dreampany.nearby.data.source.nearby.UserNearbyDataSource
import com.dreampany.network.nearby.NearbyManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by roman on 22/6/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Module(
    includes = [
        StoreModule::class
    ]
)
class DataModule {
    @Singleton
    @Provides
    @Nearby
    fun provideUserNearbyDataSource(
        context: Context,
        mapper: UserManager,
        nearby: NearbyManager
    ): UserDataSource = UserNearbyDataSource(context, mapper, nearby)
}