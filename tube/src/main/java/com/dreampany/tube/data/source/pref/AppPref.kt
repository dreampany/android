package com.dreampany.tube.data.source.pref

import android.content.Context
import com.dreampany.framework.data.source.pref.BasePref
import com.dreampany.framework.misc.constant.Constant
import com.dreampany.framework.misc.exts.currentMillis
import com.dreampany.tube.R
import com.dreampany.tube.data.model.Category
import com.dreampany.tube.misc.Constants
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by roman on 21/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class AppPref
@Inject constructor(
    context: Context,
    private val gson: Gson
) : BasePref(context) {

    override fun getPrivateName(context: Context): String = Constants.Keys.Pref.PREF

    val order: String
        get() = getPublicly(
            context.getString(R.string.key_settings_order),
            context.getString(R.string.key_settings_order_value_relevance)
        )

    @Synchronized
    fun commitCategoriesSelection() {
        setPrivately(Constants.Keys.Pref.CATEGORY, true)
    }

    val isCategoriesSelected: Boolean
        get() = getPrivately(Constants.Keys.Pref.CATEGORY, Constant.Default.BOOLEAN)

    @Synchronized
    fun commitExpireTimeOfCategory() {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.CATEGORY)
        }
        setPrivately(key.toString(), currentMillis)
    }

    @Synchronized
    fun getExpireTimeOfCategory(): Long {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.CATEGORY)
        }
        return getPrivately(key.toString(), Constant.Default.LONG)
    }

    @Synchronized
    fun commitExpireTimeOfSearch() {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.CATEGORY)
        }
        setPrivately(key.toString(), currentMillis)
    }

    @Synchronized
    fun getExpireTimeOfSearch(query: String): Long {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.SEARCH)
            append(query)
        }
        return getPrivately(key.toString(), Constant.Default.LONG)
    }

    @Synchronized
    fun commitExpireTimeOfCategoryId(categoryId: String, offset: Long) {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.VIDEO)
            append(categoryId)
            append(offset)
        }
        setPrivately(key.toString(), currentMillis)
    }

    @Synchronized
    fun getExpireTimeOfCategoryId(categoryId: String, offset: Long): Long {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.VIDEO)
            append(categoryId)
            append(offset)
        }
        return getPrivately(key.toString(), Constant.Default.LONG)
    }

    @Synchronized
    fun commitExpireTimeOfVideo(id: String) {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.VIDEO)
            append(id)
        }
        setPrivately(key.toString(), currentMillis)
    }

    @Synchronized
    fun getExpireTimeOfVideo(id: String): Long {
        val key = StringBuilder(Constants.Keys.Pref.EXPIRE).apply {
            append(Constants.Keys.Pref.VIDEO)
            append(id)
        }
        return getPrivately(key.toString(), Constant.Default.LONG)
    }

    @Synchronized
    fun commitCategories(inputs: List<Category>) {
        val json = gson.toJson(inputs)
        setPrivately(Constants.Keys.Pref.CATEGORIES, json)
    }

    val categories: List<Category>?
        get() {
            val json =
                getPrivately(Constants.Keys.Pref.CATEGORIES, Constant.Default.NULL as String?)
            if (json.isNullOrEmpty()) {
                return null
            } else {
                return gson.fromJson(json, Array<Category>::class.java).toList()
            }
        }

}