package com.dreampany.tools.data.enums.radio

import com.dreampany.common.data.enums.BaseSubtype
import kotlinx.android.parcel.Parcelize

/**
 * Created by roman on 14/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Parcelize
enum class RadioSubtype : BaseSubtype {
    DEFAULT;

    override val value: String get() = name
}