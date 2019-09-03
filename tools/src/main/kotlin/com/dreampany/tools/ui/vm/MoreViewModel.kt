package com.dreampany.tools.ui.vm

import android.app.Activity
import android.app.Application
import com.dreampany.frame.data.enums.Action
import com.dreampany.tools.data.model.More
import com.dreampany.tools.data.source.pref.Pref
import com.dreampany.tools.ui.enums.MoreType
import com.dreampany.tools.ui.model.MoreItem
import com.dreampany.frame.data.misc.StoreMapper
import com.dreampany.frame.data.source.repository.StoreRepository
import com.dreampany.frame.misc.*
import com.dreampany.frame.misc.exception.ExtraException
import com.dreampany.frame.misc.exception.MultiException
import com.dreampany.frame.ui.model.UiTask
import com.dreampany.frame.util.SettingsUtil
import com.dreampany.frame.ui.vm.BaseViewModel
import com.dreampany.network.manager.NetworkManager
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

/**
 * Created by Roman-372 on 7/24/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class MoreViewModel @Inject constructor(
    application: Application,
    rx: RxMapper,
    ex: AppExecutors,
    rm: ResponseMapper,
    val network: NetworkManager,
    val pref: Pref,
    private val storeMapper: StoreMapper,
    private val storeRepo: StoreRepository,
    @Favorite val favorites: SmartMap<String, Boolean>
) : BaseViewModel<More, MoreItem, UiTask<More>>(application, rx, ex, rm) {

    fun loads(important: Boolean) {
        if (!takeAction(important, multipleDisposable)) {
            return
        }
        val disposable = rx
            .backToMain(getItems())
            .doOnSubscribe { subscription -> postProgress(true) }
            .subscribe(
                { result ->
                    postResult(Action.GET, result)
                },
                { error ->
                    postFailures(MultiException(error, ExtraException()))
                })
        addMultipleSubscription(disposable)
    }

    private fun getItems(): Maybe<List<MoreItem>> {
        return Maybe.fromCallable {
            val items = ArrayList<MoreItem>()
            items.add(MoreItem.getItem(More(MoreType.SETTINGS)))
            items.add(MoreItem.getItem(More(MoreType.APPS)))
            items.add(MoreItem.getItem(More(MoreType.RATE_US)))
            items.add(MoreItem.getItem(More(MoreType.FEEDBACK)))
            //items.add(MoreItem.getItemBySymbolRx(new More(MoreType.INVITE)));
            items.add(MoreItem.getItem(More(MoreType.LICENSE)))
            items.add(MoreItem.getItem(More(MoreType.ABOUT)))
            items
        }
    }

    fun moreApps(activity: Activity) {
        SettingsUtil.moreApps(Objects.requireNonNull(activity))
    }

    fun rateUs(activity: Activity) {
        SettingsUtil.rateUs(activity)
    }

    fun sendFeedback(activity: Activity) {
        SettingsUtil.feedback(activity)
    }
}