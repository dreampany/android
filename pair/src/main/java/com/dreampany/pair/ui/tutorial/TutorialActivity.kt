package com.dreampany.pair.ui.tutorial

import android.os.Bundle
import android.view.View
import com.dreampany.common.ui.activity.BaseInjectorActivity
import com.dreampany.common.ui.adapter.BaseAdapter
import com.dreampany.pair.R
import com.dreampany.pair.databinding.TutorialActivityBinding

/**
 * Created by roman on 3/10/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class TutorialActivity : BaseInjectorActivity() {

    private lateinit var bind: TutorialActivityBinding
    private lateinit var tutorialAdapter: TutorialAdapter

    override fun isFullScreen(): Boolean = true

    override fun hasBinding(): Boolean = true

    override fun getLayoutId(): Int = R.layout.tutorial_activity

    override fun onStartUi(state: Bundle?) {
        initUi()
        initRecycler()
    }

    override fun onStopUi() {
    }

    private fun initUi() {
        bind = getBinding()
    }

    private fun initRecycler() {
        tutorialAdapter = TutorialAdapter(object : BaseAdapter.OnItemClickListener<TutorialItem> {
            override fun onItemClick(item: TutorialItem) {

            }

            override fun onChildItemClick(view: View, item: TutorialItem) {
            }
        })
        tutorialAdapter.add(TutorialItem(R.mipmap.ic_launcher, "Hello1", "This is Hello"))
        tutorialAdapter.add(TutorialItem(R.mipmap.ic_launcher, "Hello2", "This is Hello"))
        tutorialAdapter.add(TutorialItem(R.mipmap.ic_launcher, "Hello3", "This is Hello"))
        bind.pager.adapter = tutorialAdapter
        bind.indicator.setViewPager(bind.pager)
        tutorialAdapter.registerAdapterDataObserver(bind.indicator.adapterDataObserver)
    }
}