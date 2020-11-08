package com.dreampany.tools.ui.radio.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dreampany.framework.misc.exts.value
import com.dreampany.framework.ui.adapter.BasePagerAdapter
import com.dreampany.framework.ui.model.UiTask
import com.dreampany.tools.data.enums.Action
import com.dreampany.tools.data.enums.State
import com.dreampany.tools.data.enums.Subtype
import com.dreampany.tools.data.enums.Type
import com.dreampany.tools.data.model.radio.Page
import com.dreampany.tools.ui.radio.fragment.StationsFragment
import com.dreampany.tools.ui.radio.model.PageItem

/**
 * Created by roman on 8/11/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class PageAdapter(activity: AppCompatActivity) : BasePagerAdapter<Fragment>(activity) {

    private val pages = arrayListOf<Page>()

    fun addItems(items: List<PageItem>) {
        pages.clear()

        items.forEach {
            pages.add(it.input)
            val task = UiTask(
                Type.PAGE,
                Subtype.DEFAULT,
                State.DEFAULT,
                Action.DEFAULT,
                it.input
            )
            addItem(
                com.dreampany.framework.misc.exts.createFragment(
                    StationsFragment::class,
                    task
                ),
                it.input.title.value,
                false
            )
        }
        notifyDataSetChanged()
    }

    fun hasUpdate(inputs: List<Page>): Boolean =
        (inputs.containsAll(pages) && pages.containsAll(inputs)).not()
}