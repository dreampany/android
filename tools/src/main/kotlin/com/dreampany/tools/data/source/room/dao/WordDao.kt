package com.dreampany.tools.data.source.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.dreampany.framework.data.source.room.dao.BaseDao
import com.dreampany.tools.data.model.word.Word
import io.reactivex.Maybe

/**
 * Created by roman on 2019-08-16
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Dao
interface WordDao : BaseDao<Word> {

    @Query("select count(*) from word where id = :id limit 1")
    fun getCount(id: String): Int

    @Query("select count(*) from word where id = :id limit 1")
    fun getCountRx(id: String): Maybe<Int>

    @Query("select * from word where id = :id limit 1")
    fun getItem(id: String): Word?

    @Query("select * from word where id = :id limit 1")
    fun getItemRx(id: String): Maybe<Word>

    @Query("select * from word")
    fun getItemsRx(): Maybe<List<Word>>

    @Query("select id from word order by id asc")
    fun getRawItemsRx(): Maybe<List<String>>

    @Query("select id from word where length(id) == length(:id) order by random() limit :limit")
    fun getRawItemsByLength(id: String, limit : Long): List<String>?
}