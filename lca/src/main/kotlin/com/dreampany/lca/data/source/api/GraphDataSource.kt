package com.dreampany.lca.data.source.api

import com.dreampany.frame.data.source.api.DataSource
import com.dreampany.lca.data.model.Graph
import io.reactivex.Maybe

/**
 * Created by roman on 2019-07-14
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
interface GraphDataSource : DataSource<Graph> {
    fun getItem(slug: String, startTime: Long, endTime: Long): Graph?

    fun getItemRx(slug: String, startTime: Long, endTime: Long): Maybe<Graph>
}