package com.dreampany.common.misc.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dreampany.common.misc.func.Executors
import kotlinx.coroutines.Runnable
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Created by roman on 17/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
fun Activity?.alive() : Boolean {
    if (this == null) return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        return !(isFinishing() || isDestroyed())
    return !isFinishing()
}

fun <T : Any> Activity?.open(target: KClass<T>, finishCurrent: Boolean = false) {
    this?.run {
        startActivity(Intent(this, target.java))
        if (finishCurrent) {
            finish()
        }
    }
}

fun <T : Activity> Activity?.open(target: KClass<T>, flags: Int, finishCurrent: Boolean = false) {
    this?.run {
        startActivity(Intent(this, target.java).addFlags(flags))
        if (finishCurrent) {
            finish()
        }
    }
}

fun Activity?.clearFlags(
): Int =
    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

fun <T : Fragment> AppCompatActivity?.fragment(tag: String?): T? =
    this?.supportFragmentManager?.findFragmentByTag(tag) as T?

fun <T : Fragment> AppCompatActivity?.open(fragment: T?, @IdRes parent: Int, ex: Executors) {
    val runner = Runnable {
        this?.run {
            if (isDestroyed || isFinishing) return@Runnable
            fragment?.let {
                supportFragmentManager
                    .beginTransaction()
                    .replace(parent, it, it.javaClass.simpleName)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }
    ex.postToUi(runner)
}

fun Activity?.moreApps(devId : String) {
    if (this == null) return
    try {
        val uri = Uri.parse("market://search?q=pub:$devId")
        val market = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(market)
    } catch (error: ActivityNotFoundException) {
        Timber.e(error)
    }
}

fun Activity?.rateUs() {
    if (this == null) return
    try {
        val id: String = this.packageName
        val uri = Uri.parse("market://details?id=$id")
        val market = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(market)
    } catch (error: ActivityNotFoundException) {
        Timber.e(error)
    }
}
