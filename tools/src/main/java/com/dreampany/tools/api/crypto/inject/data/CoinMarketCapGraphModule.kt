package com.dreampany.tools.api.crypto.inject.data

import com.dreampany.tools.api.crypto.inject.CoinMarketCapAnnote
import com.dreampany.tools.api.crypto.misc.Constants
import com.dreampany.tools.api.crypto.remote.service.CoinMarketCapService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by roman on 9/11/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Module
class CoinMarketCapGraphModule {

    @Singleton
    @Provides
    @CoinMarketCapAnnote
    fun provideCoinMarketCapGraphRetrofit(gson: Gson, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.Apis.CoinMarketCap.BASE_URL)
            .client(httpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideCoinMarketCapService(@CoinMarketCapAnnote retrofit: Retrofit): CoinMarketCapService {
        return retrofit.create(CoinMarketCapService::class.java);
    }
}