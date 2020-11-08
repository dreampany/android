package com.dreampany.tools.data.source.radio.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.dreampany.framework.data.source.room.dao.BaseDao
import com.dreampany.tools.data.model.radio.Page

/**
 * Created by roman on 8/11/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Dao
interface PageDao : BaseDao<Page> {

    @get:Query("select * from page")
    val all: List<Page>?

    @Query("select * from page where id = :id limit 1")
    fun read(id: String): Page?
}