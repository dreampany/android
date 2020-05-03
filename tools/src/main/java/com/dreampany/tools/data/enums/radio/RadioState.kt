package com.dreampany.tools.data.enums.radio

import com.dreampany.framework.data.enums.BaseState
import com.dreampany.framework.misc.extension.toTitle
import kotlinx.android.parcel.Parcelize

/**
 * Created by roman on 15/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Parcelize
enum class RadioState : BaseState {
    LOCAL, TRENDS, POPULAR;

    override val value: String get() = name
    val title: String get() = name.toTitle()
}