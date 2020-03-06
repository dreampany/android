package com.dreampany.tools.data.source.api.crypto

import com.dreampany.framework.data.source.api.DataSource
import com.dreampany.framework.data.source.api.DataSourceRx
import com.dreampany.tools.data.enums.CoinSort
import com.dreampany.tools.data.enums.Currency
import com.dreampany.tools.data.enums.Order
import com.dreampany.tools.data.model.crypto.Coin
import io.reactivex.Maybe

/**
 * Created by roman on 2019-11-11
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
interface CoinDataSource : DataSource<Coin>, DataSourceRx<Coin> {

    fun getItem(currency: Currency, id: String): Coin?

    fun getItemRx(currency: Currency, id: String): Maybe<Coin>

    fun getItems(
        currency: Currency,
        sort: CoinSort,
        order: Order,
        start: Long,
        limit: Long
    ): List<Coin>?

    fun getItemsRx(
        currency: Currency,
        sort: CoinSort,
        order: Order,
        start: Long,
        limit: Long
    ): Maybe<List<Coin>>

    fun getItems(currency: Currency, ids: List<String>): List<Coin>?

    fun getItemsRx(currency: Currency, ids: List<String>): Maybe<List<Coin>>
}