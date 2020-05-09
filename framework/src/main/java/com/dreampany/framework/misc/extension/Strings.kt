package com.dreampany.framework.misc.extension

import android.util.Patterns
import com.dreampany.framework.misc.constant.Constants
import com.google.common.hash.Hashing

/**
 * Created by roman on 3/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
fun String.hash256(): String {
    return Hashing.sha256().newHasher()
        .putString(this, Charsets.UTF_8).hash().toString()
}

fun String?.string(): String {
    return this ?: Constants.Default.STRING
}

fun String?.isEquals(value: String?): Boolean {
    return this == value
}

fun String?.lastPart(denim: Char): String? = this?.split(denim)?.last()
fun String?.firstPart(denim: Char): String? = this?.split(denim)?.first()

fun String?.isEmail(): Boolean {
    return this?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: false
}

fun String.append(vararg suffixes : String) : String {
    val builder = StringBuilder(this)
    suffixes.forEach { builder.append(it) }
    return builder.toString()
}

fun String?.parseInt(): Int = this?.toInt() ?: 0

fun String.toTitle(): String {
    var space = true
    val builder = StringBuilder(this)
    val len = builder.length

    for (i in 0 until len) {
        val c = builder[i]
        if (space) {
            if (!Character.isWhitespace(c)) {
                // Convert to setTitle case and switch out of whitespace mode.
                builder.setCharAt(i, Character.toTitleCase(c))
                space = false
            }
        } else if (Character.isWhitespace(c)) {
            space = true
        } else {
            builder.setCharAt(i, Character.toLowerCase(c))
        }
    }

    return builder.toString()
}

val CharSequence?.value: String
    get() = if (this == null) Constants.Default.STRING else this.toString()

val CharSequence?.trimValue: String
    get() = if (this == null) Constants.Default.STRING else this.trim().toString()