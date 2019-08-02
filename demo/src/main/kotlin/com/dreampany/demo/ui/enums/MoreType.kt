package com.dreampany.demo.ui.enums

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by roman on 2019-07-27
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Parcelize
enum class MoreType : Parcelable {
    SETTINGS, APPS, RATE_US, FEEDBACK, INVITE, LICENSE, ABOUT
}