package com.dreampany.tools.data.source.radio.room.converters

import androidx.room.TypeConverter
import com.dreampany.framework.data.source.room.converter.Converter
import com.dreampany.tools.data.model.radio.Page

/**
 * Created by roman on 21/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class Converters : Converter() {
    @Synchronized
    @TypeConverter
    fun toString(input: Page.Type?): String? = if (input == null) null else input.name

    @Synchronized
    @TypeConverter
    fun toPageType(input: String?): Page.Type? =
        if (input.isNullOrEmpty()) null else Page.Type.valueOf(input)
}