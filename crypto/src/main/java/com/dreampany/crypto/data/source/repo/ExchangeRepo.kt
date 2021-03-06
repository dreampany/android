package com.dreampany.crypto.data.source.repo

import com.dreampany.crypto.data.source.api.ExchangeDataSource
import com.dreampany.crypto.data.source.mapper.ExchangeMapper
import com.dreampany.framework.inject.annote.Remote
import com.dreampany.framework.misc.func.ResponseMapper
import com.dreampany.framework.misc.func.RxMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by roman on 3/21/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class ExchangeRepo
@Inject constructor(
    rx: RxMapper,
    rm: ResponseMapper,
    private val mapper: ExchangeMapper,
    @Remote private val remote: ExchangeDataSource
) : ExchangeDataSource {

    @Throws
    override suspend fun getExchanges(
        fromSymbol: String,
        toSymbol: String,
        extraParams: String,
        limit: Long
    ) = withContext(
        Dispatchers.IO
    ) {
        remote.getExchanges(fromSymbol, toSymbol, extraParams, limit)
    }


}