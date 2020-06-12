package com.dreampany.tools.inject.data

import android.app.Application
import android.content.Context
import com.dreampany.framework.inject.annote.Remote
import com.dreampany.framework.inject.annote.Room
import com.dreampany.framework.misc.func.Keys
import com.dreampany.framework.misc.func.Parser
import com.dreampany.network.manager.NetworkManager
import com.dreampany.tools.api.crypto.inject.data.CoinMarketCapModule
import com.dreampany.tools.api.crypto.inject.data.CryptoCompareModule
import com.dreampany.tools.api.crypto.inject.data.GeckoModule
import com.dreampany.tools.api.crypto.remote.service.CoinMarketCapService
import com.dreampany.tools.api.crypto.remote.service.CryptoCompareService
import com.dreampany.tools.data.source.crypto.api.CoinDataSource
import com.dreampany.tools.data.source.crypto.api.ExchangeDataSource
import com.dreampany.tools.data.source.crypto.api.TradeDataSource
import com.dreampany.tools.data.source.crypto.mapper.CoinMapper
import com.dreampany.tools.data.source.crypto.mapper.ExchangeMapper
import com.dreampany.tools.data.source.crypto.mapper.TradeMapper
import com.dreampany.tools.data.source.crypto.remote.CoinRemoteDataSource
import com.dreampany.tools.data.source.crypto.remote.ExchangeRemoteDataSource
import com.dreampany.tools.data.source.crypto.remote.TradeRemoteDataSource
import com.dreampany.tools.data.source.crypto.room.CoinRoomDataSource
import com.dreampany.tools.data.source.crypto.room.dao.CoinDao
import com.dreampany.tools.data.source.crypto.room.dao.QuoteDao
import com.dreampany.tools.data.source.crypto.room.database.DatabaseManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by roman on 21/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Module(
    includes = [
        CoinMarketCapModule::class,
        CryptoCompareModule::class,
        GeckoModule::class
    ]
)
class CryptoModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): DatabaseManager =
        DatabaseManager.getInstance(application)

    @Provides
    @Singleton
    fun provideCoinDao(database: DatabaseManager): CoinDao = database.coinDao()

    @Provides
    @Singleton
    fun provideQuoteDao(database: DatabaseManager): QuoteDao = database.quoteDao()

    @Singleton
    @Provides
    @Room
    fun provideCoinRoomDataSource(
        mapper: CoinMapper,
        dao: CoinDao,
        quoteDao: QuoteDao
    ): CoinDataSource = CoinRoomDataSource(mapper, dao, quoteDao)

    @Singleton
    @Provides
    @Remote
    fun provideCoinRemoteDataSource(
        context: Context,
        network: NetworkManager,
        parser: Parser,
        keys: Keys,
        mapper: CoinMapper,
        service: CoinMarketCapService
    ): CoinDataSource = CoinRemoteDataSource(context, network, parser, keys, mapper, service)

    @Singleton
    @Provides
    @Remote
    fun provideTradeRemoteDataSource(
        context: Context,
        network: NetworkManager,
        parser: Parser,
        keys: Keys,
        mapper: TradeMapper,
        service: CryptoCompareService
    ): TradeDataSource = TradeRemoteDataSource(context, network, parser, keys, mapper, service)

    @Singleton
    @Provides
    @Remote
    fun provideExchangeRemoteDataSource(
        context: Context,
        network: NetworkManager,
        parser: Parser,
        keys: Keys,
        mapper: ExchangeMapper,
        service: CryptoCompareService
    ): ExchangeDataSource =
        ExchangeRemoteDataSource(context, network, parser, keys, mapper, service)
/*

    @Singleton
    @Provides
    @Database
    fun provideDatabaseCoinDataSource(
        network: NetworkManager,
        mapper: CoinMapper,
        database: RxFirebaseDatabase
    ): CoinDataSource {
        return DatabaseCoinDataSource(network, mapper, database)
    }

    @Singleton
    @Provides
    @Remote
    fun provideRemoteTradeDataSource(
        context: Context,
        network: NetworkManager,
        keyM: KeyManager,
        mapper: TradeMapper,
        service: CryptoCompareService
    ): TradeDataSource {
        return TradeRemoteDataSource(
            context,
            network,
            keyM,
            mapper,
            service
        )
    }

    @Singleton
    @Provides
    @Remote
    fun provideExchangeRemoteDataSource(
        context: Context,
        network: NetworkManager,
        keyM: KeyManager,
        mapper: ExchangeMapper,
        service: CryptoCompareService
    ): ExchangeDataSource {
        return ExchangeRemoteDataSource(
            context,
            network,
            keyM,
            mapper,
            service
        )
    }*/
}