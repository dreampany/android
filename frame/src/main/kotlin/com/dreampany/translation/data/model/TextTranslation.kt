package com.dreampany.translation.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.dreampany.translation.misc.Constants
import com.google.common.base.Objects
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

/**
 * Created by roman on 2019-07-03
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Entity(
    indices = [Index(
        value = [Constants.Translation.SOURCE, Constants.Translation.TARGET, Constants.Translation.INPUT],
        unique = true
    )],
    primaryKeys = [Constants.Translation.SOURCE, Constants.Translation.TARGET, Constants.Translation.INPUT]
)
@IgnoreExtraProperties
@Parcelize
data class TextTranslation(
    override val id: String,
    override val time: Long,
    override val source: String,
    override val target: String,
    val input: String,
    val output: String
) : Translation() {

    @Ignore
    constructor() : this("", 0L, "", "", "", "") {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val item = other as TextTranslation
        return Objects.equal(item.source, source)
                && Objects.equal(item.target, target)
                && Objects.equal(item.input, input)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(source, target, input)
    }
}