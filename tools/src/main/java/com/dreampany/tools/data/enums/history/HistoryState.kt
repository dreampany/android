package com.dreampany.tools.data.enums.history

import com.dreampany.framework.data.enums.BaseState
import com.dreampany.framework.misc.exts.title
import kotlinx.android.parcel.Parcelize

/**
 * Created by roman on 26/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Parcelize
enum class HistoryState : BaseState {
    DEFAULT, FAVORITE, EVENT, BIRTH, DEATH;

    override val value: String get() = name
    val title : String get() = name.title
}