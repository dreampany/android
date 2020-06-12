package com.dreampany.tools.ui.crypto.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dreampany.framework.ui.adapter.BasePagerAdapter
import com.dreampany.framework.ui.model.UiTask
import com.dreampany.tools.R
import com.dreampany.tools.data.enums.crypto.CryptoAction
import com.dreampany.tools.data.enums.crypto.CryptoState
import com.dreampany.tools.data.enums.crypto.CryptoSubtype
import com.dreampany.tools.data.enums.crypto.CryptoType
import com.dreampany.tools.data.model.crypto.Coin
import com.dreampany.tools.ui.crypto.fragment.InfoFragment
import com.dreampany.tools.ui.crypto.fragment.MarketFragment
import com.dreampany.tools.ui.crypto.fragment.TickersFragment

/**
 * Created by roman on 27/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class CoinPagerAdapter(activity: AppCompatActivity) : BasePagerAdapter<Fragment>(activity) {

    fun addItems(input: Coin) {
        val info = UiTask(
            CryptoType.COIN,
            CryptoSubtype.INFO,
            CryptoState.DEFAULT,
            CryptoAction.DEFAULT,
            input
        )
        val market = UiTask(
            CryptoType.COIN,
            CryptoSubtype.MARKET,
            CryptoState.DEFAULT,
            CryptoAction.DEFAULT,
            input
        )
        addItem(
            com.dreampany.framework.misc.extension.createFragment(
                InfoFragment::class,
                info
            ),
            R.string.title_coin_info,
            true
        )
        addItem(
            com.dreampany.framework.misc.extension.createFragment(
                TickersFragment::class,
                market
            ),
            R.string.title_coin_markets,
            true
        )
    }
}