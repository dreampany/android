package com.dreampany.demo.ui.fragment

import android.os.Bundle
import com.dreampany.frame.api.service.JobManager
import com.dreampany.frame.misc.ActivityScope
import com.dreampany.frame.misc.RxMapper
import com.dreampany.frame.ui.fragment.BaseMenuFragment
import com.dreampany.demo.R
import com.dreampany.demo.data.source.pref.Pref
import com.dreampany.demo.misc.Constants
import com.dreampany.demo.service.NotifyService
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Roman-372 on 7/29/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@ActivityScope
class SettingsFragment @Inject constructor() : BaseMenuFragment() {

    @Inject
    internal lateinit var rx: RxMapper
    @Inject
    internal lateinit var job: JobManager
    @Inject
    internal lateinit var pref: Pref
    private val disposables: CompositeDisposable

    init {
        disposables = CompositeDisposable()
    }

    override fun getPrefLayoutId(): Int {
        return R.xml.settings
    }

    override fun getScreen(): String {
        return Constants.settings(context!!)
    }

    override fun onStartUi(state: Bundle?) {
        ex.postToUi { this.initView() }
    }

    override fun onStopUi() {
        disposables.clear()
    }

    private fun initView() {
        setTitle(R.string.settings)
        val notifyEvent = getString(R.string.key_notification)
        val notifyFlowable = pref.observePublicly(notifyEvent, Boolean::class.java, true)

        disposables.add(rx
            .backToMain(notifyFlowable)
            .subscribe { enabled -> adjustNotify() })
    }

    private val runner = Runnable { this.configJob() }

    private fun configJob() {
        if (pref.hasNotification()) {
            job.create(
                Constants.Tag.NOTIFY_SERVICE,
                NotifyService::class,
                Constants.Delay.Notify.toInt(),
                Constants.Period.Notify.toInt()
            )
        } else {
            job.cancel(Constants.Tag.NOTIFY_SERVICE)
        }
    }

    private fun adjustNotify() {
        ex.postToUi(runner, 2000)
    }
}