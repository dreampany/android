package com.dreampany.tools.data.source.api

import android.graphics.Bitmap
import com.dreampany.framework.data.source.api.DataSource
import com.dreampany.framework.data.enums.Source
import com.dreampany.tools.data.model.Word
import com.google.firebase.firestore.FirebaseFirestoreException
import io.reactivex.Maybe

/**
 * Created by roman on 2019-08-15
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
interface WordDataSource : DataSource<Word> {

    fun isValid(id: String): Boolean

    fun isExists(id: String): Boolean

    fun getItems(ids: List<String>): List<Word>?

    fun getItemsRx(ids: List<String>): Maybe<List<Word>>

    fun getSearchItems(query: String, limit: Long): List<Word>?

    fun getCommonItems(): List<Word>?

    fun getAlphaItems(): List<Word>?

    fun getItemsRx(bitmap: Bitmap): Maybe<List<Word>>

    fun getRawWords(): List<String>?

    fun getRawWordsRx(): Maybe<List<String>>

    fun getRawItemsByLength(id: String, limit: Long): List<String>?

    fun track(id: String, weight: Int, source: Source): Long

    fun trackRx(id: String, weight: Int, source: Source): Maybe<Long>

    @Throws(Throwable::class)
    fun getTracks(startAt: String, limit: Long): List<Pair<String, Map<String, Any>>>?

    @Throws(Throwable::class)
    fun getTracksRx(startAt: String, limit: Long): Maybe<List<Pair<String, Map<String, Any>>>>
}